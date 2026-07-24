package com.ringstory.ringtree.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ringstory.ringtree.dto.RingTreeNodeDTO;
import com.ringstory.ringtree.service.RingTreeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 年轮树服务实现类
 * 支持 Redis 缓存 + 稀疏月份合并
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RingTreeServiceImpl implements RingTreeService {

    private final StringRedisTemplate redisTemplate;
    private final JdbcTemplate jdbcTemplate;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String SEASONS = "春夏秋冬";
    private static final String CACHE_KEY_PREFIX = "ringtree:";
    private static final int CACHE_TTL_HOURS = 1;
    private static final int SPARSE_THRESHOLD = 3; // 月份照片数 < 3 视为稀疏

    @Override
    public RingTreeNodeDTO buildTree(Long familyId) {
        String cacheKey = CACHE_KEY_PREFIX + familyId;

        // 1. 尝试从 Redis 读取缓存
        try {
            String cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return objectMapper.readValue(cached, RingTreeNodeDTO.class);
            }
        } catch (Exception e) {
            log.warn("读取年轮树缓存失败: familyId={}, error={}", familyId, e.getMessage());
        }

        // 2. 缓存未命中，从 MySQL 构建
        RingTreeNodeDTO root = buildTreeFromDb(familyId);

        // 3. 回填 Redis 缓存（TTL 1小时）
        try {
            redisTemplate.opsForValue().set(cacheKey,
                    objectMapper.writeValueAsString(root),
                    CACHE_TTL_HOURS, TimeUnit.HOURS);
            log.info("年轮树缓存回填成功: familyId={}", familyId);
        } catch (Exception e) {
            log.warn("写入年轮树缓存失败: familyId={}, error={}", familyId, e.getMessage());
        }

        return root;
    }

    /**
     * 从数据库构建年轮树
     */
    private RingTreeNodeDTO buildTreeFromDb(Long familyId) {
        // 查询所有照片的拍摄年份分布
        List<Map<String, Object>> years = jdbcTemplate.queryForList(
                "SELECT YEAR(shoot_time) as y, COUNT(*) as cnt "
                        + "FROM t_photo_2026 WHERE family_id=? AND deleted_at IS NULL "
                        + "GROUP BY YEAR(shoot_time) ORDER BY y DESC",
                familyId);

        List<RingTreeNodeDTO> yearNodes = years.stream().map(row -> {
            int year = ((Number) row.get("y")).intValue();
            int yearCnt = ((Number) row.get("cnt")).intValue();
            List<RingTreeNodeDTO> seasonNodes = buildSeasonNodes(familyId, year);
            return new RingTreeNodeDTO(String.valueOf(year), "year", yearCnt, seasonNodes);
        }).collect(Collectors.toList());

        int totalCount = yearNodes.stream().mapToInt(RingTreeNodeDTO::getPhotoCount).sum();
        return new RingTreeNodeDTO("家庭年轮", "root", totalCount, yearNodes);
    }

    /**
     * 构建季节节点（含稀疏月份合并）
     */
    private List<RingTreeNodeDTO> buildSeasonNodes(Long familyId, int year) {
        List<RingTreeNodeDTO> seasons = new ArrayList<>();
        int[][] seasonRanges = {{3, 5}, {6, 8}, {9, 11}, {12, 2}};

        for (int i = 0; i < 4; i++) {
            int startMonth = seasonRanges[i][0];
            int endMonth = seasonRanges[i][1];
            String seasonLabel = SEASONS.charAt(i) + "季";

            String sql;
            if (startMonth <= endMonth) {
                sql = "SELECT MONTH(shoot_time) as m, COUNT(*) as cnt FROM t_photo_2026 "
                        + "WHERE family_id=? AND YEAR(shoot_time)=? "
                        + "AND MONTH(shoot_time) BETWEEN ? AND ? "
                        + "AND deleted_at IS NULL GROUP BY MONTH(shoot_time) ORDER BY m";
            } else {
                // 冬季跨年: 12月 or 1月, 2月
                sql = "SELECT MONTH(shoot_time) as m, COUNT(*) as cnt FROM t_photo_2026 "
                        + "WHERE family_id=? AND YEAR(shoot_time)=? "
                        + "AND (MONTH(shoot_time) >= ? OR MONTH(shoot_time) <= ?) "
                        + "AND deleted_at IS NULL GROUP BY MONTH(shoot_time) ORDER BY m";
            }

            List<Map<String, Object>> months = jdbcTemplate.queryForList(
                    sql, familyId, year, startMonth, endMonth);
            if (months.isEmpty()) {
                continue;
            }

            int seasonCnt = months.stream()
                    .mapToInt(m -> ((Number) m.get("cnt")).intValue()).sum();

            // 稀疏月份合并：照片数 < 3 的月份合并为"季度碎片"
            List<RingTreeNodeDTO> monthNodes = new ArrayList<>();
            List<Map<String, Object>> sparseMonths = new ArrayList<>();

            for (Map<String, Object> m : months) {
                int cnt = ((Number) m.get("cnt")).intValue();
                if (cnt < SPARSE_THRESHOLD) {
                    sparseMonths.add(m);
                } else {
                    int month = ((Number) m.get("m")).intValue();
                    List<RingTreeNodeDTO> dayNodes = buildDayNodes(familyId, year, month);
                    monthNodes.add(new RingTreeNodeDTO(month + "月", "month", cnt, dayNodes));
                }
            }

            // 将稀疏月份合并为一个"碎片"节点
            if (!sparseMonths.isEmpty()) {
                int sparseCnt = sparseMonths.stream()
                        .mapToInt(m -> ((Number) m.get("cnt")).intValue()).sum();
                String sparseLabel = sparseMonths.stream()
                        .map(m -> ((Number) m.get("m")).intValue() + "月")
                        .collect(Collectors.joining(","));
                monthNodes.add(new RingTreeNodeDTO(
                        "碎片(" + sparseLabel + ")", "month_sparse", sparseCnt, new ArrayList<>()));
            }

            seasons.add(new RingTreeNodeDTO(seasonLabel, "season", seasonCnt, monthNodes));
        }
        return seasons;
    }

    /**
     * 构建日期节点
     */
    private List<RingTreeNodeDTO> buildDayNodes(Long familyId, int year, int month) {
        List<Map<String, Object>> days = jdbcTemplate.queryForList(
                "SELECT DAY(shoot_time) as d, COUNT(*) as cnt FROM t_photo_2026 "
                        + "WHERE family_id=? AND YEAR(shoot_time)=? AND MONTH(shoot_time)=? "
                        + "AND deleted_at IS NULL GROUP BY DAY(shoot_time) ORDER BY d",
                familyId, year, month);
        return days.stream().map(row ->
                new RingTreeNodeDTO(
                        ((Number) row.get("d")).intValue() + "日",
                        "day",
                        ((Number) row.get("cnt")).intValue(),
                        new ArrayList<>())
        ).collect(Collectors.toList());
    }
}
