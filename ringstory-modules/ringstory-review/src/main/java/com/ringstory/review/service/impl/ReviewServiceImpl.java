package com.ringstory.review.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ringstory.review.entity.MomentsReviewEntity;
import com.ringstory.review.mapper.MomentsReviewMapper;
import com.ringstory.review.service.PosterGenerator;
import com.ringstory.review.service.ReviewService;
import com.ringstory.review.service.VideoGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 放映室服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl extends ServiceImpl<MomentsReviewMapper, MomentsReviewEntity> implements ReviewService {

    private final PosterGenerator posterGenerator;
    private final VideoGenerator videoGenerator;
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

        // 生成海报（降级方案）
        try {
            String posterUrl = posterGenerator.generatePoster(List.of(), review.getTitle(), "grid");
            review.setCoverUrl(posterUrl);
        } catch (Exception e) {
            log.warn("海报生成失败: reviewId={}", review.getId(), e);
        }

        review.setStatus(1);
        review.setGeneratedAt(LocalDateTime.now());
        updateById(review);

        // 发送回顾完成事件（通知全体成员）
        sendReviewCompleteEvent(familyId, review.getTitle());
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

        try {
            String posterUrl = posterGenerator.generatePoster(List.of(), review.getTitle(), "collage");
            review.setCoverUrl(posterUrl);
        } catch (Exception e) {
            log.warn("海报生成失败: reviewId={}", review.getId(), e);
        }

        review.setStatus(1);
        review.setGeneratedAt(LocalDateTime.now());
        updateById(review);

        sendReviewCompleteEvent(familyId, review.getTitle());
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

        try {
            String posterUrl = posterGenerator.generatePoster(List.of(), review.getTitle(), "timeline");
            review.setCoverUrl(posterUrl);
        } catch (Exception e) {
            log.warn("海报生成失败: reviewId={}", review.getId(), e);
        }

        review.setStatus(1);
        review.setGeneratedAt(LocalDateTime.now());
        updateById(review);

        sendReviewCompleteEvent(familyId, review.getTitle());
    }

    @Override
    public List<MomentsReviewEntity> getReviews(Long familyId) {
        return lambdaQuery().eq(MomentsReviewEntity::getFamilyId, familyId)
                .orderByDesc(MomentsReviewEntity::getCreateTime).list();
    }

    /**
     * 发送回顾完成事件到通知服务
     */
    private void sendReviewCompleteEvent(Long familyId, String reviewTitle) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "review_complete");
            event.put("familyId", familyId);
            event.put("reviewTitle", reviewTitle);
            // memberIds 留空，由通知服务查询家庭成员
            event.put("memberIds", List.of());
            rocketMQTemplate.convertAndSend("notification_trigger",
                    objectMapper.writeValueAsString(event));
            log.info("回顾完成事件已发送: familyId={}, title={}", familyId, reviewTitle);
        } catch (Exception e) {
            log.error("发送回顾完成事件失败", e);
        }
    }
}
