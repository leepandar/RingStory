package com.ringstory.ringtree.service;
import com.ringstory.ringtree.dto.RingTreeNode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RingTreeService {
    private final StringRedisTemplate redisTemplate;
    private final JdbcTemplate jdbcTemplate;

    private static final String SEASONS = "春夏秋冬";

    public RingTreeNode buildTree(Long familyId) {
        // 1. 尝试从 Redis 读取缓存
        String cacheKey = "ringtree:" + familyId;
        // String cached = redisTemplate.opsForValue().get(cacheKey);
        // if (cached != null) return JSON.parseObject(cached, RingTreeNode.class);

        // 2. 从 MySQL 查询所有照片的拍摄年份分布
        List<Map<String, Object>> years = jdbcTemplate.queryForList(
            "SELECT YEAR(shoot_time) as y, COUNT(*) as cnt FROM t_photo_2026 WHERE family_id=? AND deleted_at IS NULL GROUP BY YEAR(shoot_time) ORDER BY y DESC",
            familyId);

        List<RingTreeNode> yearNodes = years.stream().map(row -> {
            int year = ((Number) row.get("y")).intValue();
            int yearCnt = ((Number) row.get("cnt")).intValue();
            List<RingTreeNode> seasonNodes = buildSeasonNodes(familyId, year);
            return new RingTreeNode(String.valueOf(year), "year", yearCnt, seasonNodes);
        }).collect(Collectors.toList());

        RingTreeNode root = new RingTreeNode("家庭年轮", "root", yearNodes.stream().mapToInt(RingTreeNode::getPhotoCount).sum(), yearNodes);
        // 3. 缓存到 Redis（TTL 1小时）
        // redisTemplate.opsForValue().set(cacheKey, JSON.toJSONString(root), 1, java.util.concurrent.TimeUnit.HOURS);
        return root;
    }

    private List<RingTreeNode> buildSeasonNodes(Long familyId, int year) {
        List<RingTreeNode> seasons = new ArrayList<>();
        int[][] seasonRanges = {{3,5},{6,8},{9,11},{12,2}}; // 春、夏、秋、冬
        for (int i = 0; i < 4; i++) {
            int s = seasonRanges[i][0], e = seasonRanges[i][1];
            String seasonLabel = SEASONS.charAt(i) + "季";
            int startMonth = s, endMonth = e;
            if (s > e) { // 冬季跨年
                endMonth = 2;
            }
            String sql = "SELECT MONTH(shoot_time) as m, COUNT(*) as cnt FROM t_photo_2026 " +
                         "WHERE family_id=? AND YEAR(shoot_time)=? AND MONTH(shoot_time) BETWEEN ? AND ? AND deleted_at IS NULL GROUP BY MONTH(shoot_time) ORDER BY m";
            List<Map<String, Object>> months = jdbcTemplate.queryForList(sql, familyId, year, startMonth, endMonth);
            if (months.isEmpty()) continue;

            int seasonCnt = months.stream().mapToInt(m -> ((Number)m.get("cnt")).intValue()).sum();
            List<RingTreeNode> monthNodes = months.stream().map(m -> {
                int month = ((Number)m.get("cnt")).intValue();
                String monthLabel = ((Number)m.get("m")).intValue() + "月";
                List<RingTreeNode> dayNodes = buildDayNodes(familyId, year, ((Number)m.get("m")).intValue());
                return new RingTreeNode(monthLabel, "month", ((Number)m.get("cnt")).intValue(), dayNodes);
            }).collect(Collectors.toList());
            seasons.add(new RingTreeNode(seasonLabel, "season", seasonCnt, monthNodes));
        }
        return seasons;
    }

    private List<RingTreeNode> buildDayNodes(Long familyId, int year, int month) {
        List<Map<String, Object>> days = jdbcTemplate.queryForList(
            "SELECT DAY(shoot_time) as d, COUNT(*) as cnt FROM t_photo_2026 " +
            "WHERE family_id=? AND YEAR(shoot_time)=? AND MONTH(shoot_time)=? AND deleted_at IS NULL GROUP BY DAY(shoot_time) ORDER BY d",
            familyId, year, month);
        return days.stream().map(row ->
            new RingTreeNode(((Number)row.get("d")).intValue() + "日", "day", ((Number)row.get("cnt")).intValue(), new ArrayList<>())
        ).collect(Collectors.toList());
    }
}
