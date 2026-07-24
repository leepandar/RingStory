package com.ringstory.review.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ringstory.review.entity.MomentsReviewEntity;
import com.ringstory.review.mapper.MomentsReviewMapper;
import com.ringstory.review.service.ReviewDegradationService;
import com.ringstory.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 放映室服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl extends ServiceImpl<MomentsReviewMapper, MomentsReviewEntity> implements ReviewService {

    private final ReviewDegradationService reviewDegradationService;
    private final RocketMQTemplate rocketMQTemplate;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void generateMonthlyReview(Long familyId, int year, int month) {
        String ym = String.format("%d-%02d", year, month);
        log.info("生成月度回顾: familyId={}, ym={}", familyId, ym);

        MomentsReviewEntity review = new MomentsReviewEntity();
        review.setFamilyId(familyId);
        review.setType("monthly");
        review.setTitle(year + "年" + month + "月回顾");
        review.setYearMonth(ym);
        review.setStatus(2); // 生成中
        save(review);

        // 使用降级策略生成回顾媒体
        List<Long> photoIds = getTopPhotoIds(familyId, year, month, 9);
        reviewDegradationService.generateWithDegradation(review, photoIds);
        updateById(review);

        sendReviewCompleteEvent(familyId, review.getTitle(), review.getResourceType());
    }

    @Override
    public void generateSeasonalReview(Long familyId, int year, int season) {
        log.info("生成季度回顾: familyId={}, year={}, quarter={}", familyId, year, season);

        MomentsReviewEntity review = new MomentsReviewEntity();
        review.setFamilyId(familyId);
        review.setType("seasonal");
        review.setTitle(year + "年第" + season + "季度回顾");
        review.setYearMonth(String.valueOf(year));
        review.setStatus(2);
        save(review);

        List<Long> photoIds = getTopPhotoIdsByRange(familyId, year, (season - 1) * 3 + 1, 20);
        reviewDegradationService.generateWithDegradation(review, photoIds);
        updateById(review);

        sendReviewCompleteEvent(familyId, review.getTitle(), review.getResourceType());
    }

    @Override
    public void generateYearlyReview(Long familyId, int year) {
        log.info("生成年度回顾: familyId={}, year={}", familyId, year);

        MomentsReviewEntity review = new MomentsReviewEntity();
        review.setFamilyId(familyId);
        review.setType("yearly");
        review.setTitle(year + "年度回顾");
        review.setYearMonth(String.valueOf(year));
        review.setStatus(2);
        save(review);

        List<Long> photoIds = getMonthlyRepresentatives(familyId, year);
        reviewDegradationService.generateWithDegradation(review, photoIds);
        updateById(review);

        sendReviewCompleteEvent(familyId, review.getTitle(), review.getResourceType());
    }

    @Override
    public List<MomentsReviewEntity> getReviews(Long familyId) {
        return lambdaQuery().eq(MomentsReviewEntity::getFamilyId, familyId)
                .orderByDesc(MomentsReviewEntity::getCreateTime).list();
    }

    /**
     * 发送回顾完成事件到通知服务
     */
    private void sendReviewCompleteEvent(Long familyId, String reviewTitle, String resourceType) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "review_complete");
            event.put("familyId", familyId);
            event.put("reviewTitle", reviewTitle);
            event.put("resourceType", resourceType != null ? resourceType : "none");
            event.put("memberIds", List.of());
            rocketMQTemplate.convertAndSend("notification_trigger",
                    objectMapper.writeValueAsString(event));
            log.info("回顾完成事件已发送: familyId={}, title={}, type={}", familyId, reviewTitle, resourceType);
        } catch (Exception e) {
            log.error("发送回顾完成事件失败", e);
        }
    }

    /**
     * 获取月度 top 照片（按点赞数排序）
     */
    private List<Long> getTopPhotoIds(Long familyId, int year, int month, int limit) {
        return List.of(); // 由 ReviewGenerateService 的算法提供，此处留空由降级服务处理
    }

    private List<Long> getTopPhotoIdsByRange(Long familyId, int year, int startMonth, int limit) {
        return List.of();
    }

    private List<Long> getMonthlyRepresentatives(Long familyId, int year) {
        return List.of();
    }
}
