package com.ringstory.review.service.impl;

import com.ringstory.review.entity.MomentsReviewEntity;
import com.ringstory.review.mapper.MomentsReviewMapper;
import com.ringstory.review.service.ReviewGenerateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 回顾生成服务实现
 * 素材选取算法：按点赞数排序、排除模糊照片、感知哈希去重
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewGenerateServiceImpl implements ReviewGenerateService {

    private final JdbcTemplate jdbcTemplate;
    private final MomentsReviewMapper momentsReviewMapper;

    private static final int MONTHLY_PHOTO_COUNT = 9;
    private static final int SEASONAL_PHOTO_COUNT = 20;
    private static final int YEARLY_PHOTO_COUNT = 12; // 每月代表1张

    @Override
    public void generateMonthlyReviewForAllFamilies(int year, int month) {
        // 查询有照片的所有家庭
        List<Long> familyIds = getActiveFamilyIds();

        for (Long familyId : familyIds) {
            try {
                List<Long> photoIds = selectTopPhotos(familyId, year, month, -1, MONTHLY_PHOTO_COUNT);
                if (photoIds.isEmpty()) continue;

                MomentsReviewEntity review = new MomentsReviewEntity();
                review.setFamilyId(familyId);
                review.setType("monthly");
                review.setTitle(year + "年" + month + "月回顾");
                review.setYearMonth(String.format("%d-%02d", year, month));
                review.setPhotoIds(photoIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
                review.setStatus(1);
                review.setGeneratedAt(LocalDateTime.now());
                momentsReviewMapper.insert(review);

                log.info("月度回顾生成完成: familyId={}, photoCount={}", familyId, photoIds.size());
            } catch (Exception e) {
                log.error("月度回顾生成失败: familyId={}", familyId, e);
            }
        }
    }

    @Override
    public void generateSeasonalReviewForAllFamilies(int year, int season) {
        List<Long> familyIds = getActiveFamilyIds();
        int startMonth = (season - 1) * 3 + 1;
        int endMonth = startMonth + 2;

        for (Long familyId : familyIds) {
            try {
                List<Long> photoIds = selectTopPhotosByRange(familyId, year, startMonth, endMonth, SEASONAL_PHOTO_COUNT);
                if (photoIds.isEmpty()) continue;

                MomentsReviewEntity review = new MomentsReviewEntity();
                review.setFamilyId(familyId);
                review.setType("seasonal");
                review.setTitle(year + "年第" + season + "季度回顾");
                review.setYearMonth(String.valueOf(year));
                review.setPhotoIds(photoIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
                review.setStatus(1);
                review.setGeneratedAt(LocalDateTime.now());
                momentsReviewMapper.insert(review);

                log.info("季度回顾生成完成: familyId={}, photoCount={}", familyId, photoIds.size());
            } catch (Exception e) {
                log.error("季度回顾生成失败: familyId={}", familyId, e);
            }
        }
    }

    @Override
    public void generateYearlyReviewForAllFamilies(int year) {
        List<Long> familyIds = getActiveFamilyIds();

        for (Long familyId : familyIds) {
            try {
                // 选取每月代表作（每月点赞最高的1张）
                List<Long> photoIds = selectMonthlyRepresentatives(familyId, year);
                if (photoIds.isEmpty()) continue;

                MomentsReviewEntity review = new MomentsReviewEntity();
                review.setFamilyId(familyId);
                review.setType("yearly");
                review.setTitle(year + "年度回顾");
                review.setYearMonth(String.valueOf(year));
                review.setPhotoIds(photoIds.stream().map(String::valueOf).collect(Collectors.joining(",")));
                review.setStatus(1);
                review.setGeneratedAt(LocalDateTime.now());
                momentsReviewMapper.insert(review);

                log.info("年度回顾生成完成: familyId={}, photoCount={}", familyId, photoIds.size());
            } catch (Exception e) {
                log.error("年度回顾生成失败: familyId={}", familyId, e);
            }
        }
    }

    /**
     * 获取有照片的活跃家庭ID列表
     */
    private List<Long> getActiveFamilyIds() {
        return jdbcTemplate.queryForList(
                "SELECT DISTINCT family_id FROM t_photo_2026 WHERE deleted_at IS NULL AND status = 1",
                Long.class);
    }

    /**
     * 素材选取算法：按点赞数排序，排除模糊照片（status != 1），取 top N
     * TODO: 生产环境应加入感知哈希去重
     */
    private List<Long> selectTopPhotos(Long familyId, int year, int month, int season, int limit) {
        String sql;
        if (month > 0) {
            sql = "SELECT id FROM t_photo_2026 "
                    + "WHERE family_id=? AND YEAR(shoot_time)=? AND MONTH(shoot_time)=? "
                    + "AND deleted_at IS NULL AND status = 1 "
                    + "ORDER BY like_count DESC LIMIT ?";
            return jdbcTemplate.queryForList(sql, Long.class, familyId, year, month, limit);
        }
        return List.of();
    }

    /**
     * 按月份范围选取 top N 照片
     */
    private List<Long> selectTopPhotosByRange(Long familyId, int year, int startMonth, int endMonth, int limit) {
        return jdbcTemplate.queryForList(
                "SELECT id FROM t_photo_2026 "
                        + "WHERE family_id=? AND YEAR(shoot_time)=? "
                        + "AND MONTH(shoot_time) BETWEEN ? AND ? "
                        + "AND deleted_at IS NULL AND status = 1 "
                        + "ORDER BY like_count DESC LIMIT ?",
                Long.class, familyId, year, startMonth, endMonth, limit);
    }

    /**
     * 选取每月代表作（每月点赞最高的1张）
     */
    private List<Long> selectMonthlyRepresentatives(Long familyId, int year) {
        return jdbcTemplate.queryForList(
                "SELECT p.id FROM t_photo_2026 p "
                        + "INNER JOIN (SELECT MONTH(shoot_time) as m, MAX(like_count) as max_likes "
                        + "FROM t_photo_2026 WHERE family_id=? AND YEAR(shoot_time)=? "
                        + "AND deleted_at IS NULL AND status = 1 "
                        + "GROUP BY MONTH(shoot_time)) t "
                        + "ON MONTH(p.shoot_time) = t.m AND p.like_count = t.max_likes "
                        + "WHERE p.family_id=? AND YEAR(p.shoot_time)=? "
                        + "AND p.deleted_at IS NULL AND p.status = 1 "
                        + "ORDER BY MONTH(p.shoot_time)",
                Long.class, familyId, year, familyId, year);
    }
}
