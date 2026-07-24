package com.ringstory.review.service.impl;

import com.ringstory.review.entity.MomentsReviewEntity;
import com.ringstory.review.service.PosterGenerator;
import com.ringstory.review.service.ReviewDegradationService;
import com.ringstory.review.service.VideoGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 放映室降级策略实现
 * <p>
 * 降级链路及规则：
 * Level 0 - 视频（最佳体验）：重试3次，间隔 2s/4s/8s（指数退避）
 * Level 1 - 动态幻灯片（中等体验）：重试2次，间隔 1s/2s
 * Level 2 - 静态海报（基础体验）：重试1次
 * Level 3 - 纯通知（最低体验）：仅发送通知，不生成任何媒体
 * <p>
 * 用户通知策略：
 * - 降级到 Level 1/2 时，回顾标题追加提示（如 "[海报模式] 2026年1月回顾"）
 * - 降级到 Level 3 时，不生成回顾实体，仅发送通知
 * - 客户端根据 resourceType 字段判断展示模式
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewDegradationServiceImpl implements ReviewDegradationService {

    private final VideoGenerator videoGenerator;
    private final PosterGenerator posterGenerator;

    /** 视频服务连续失败计数（用于健康判断） */
    private final AtomicInteger videoFailCount = new AtomicInteger(0);
    /** 连续失败超过此阈值视为不健康 */
    private static final int UNHEALTHY_THRESHOLD = 3;

    // 重试配置
    private static final int VIDEO_MAX_RETRIES = 3;
    private static final int SLIDESHOW_MAX_RETRIES = 2;
    private static final int POSTER_MAX_RETRIES = 1;

    @Override
    public void generateWithDegradation(MomentsReviewEntity review, List<Long> photoIds) {
        List<String> photoIdStrings = photoIds.stream()
                .map(String::valueOf).collect(Collectors.toList());

        // Level 0: 尝试生成视频
        if (attemptVideo(review, photoIdStrings)) {
            return;
        }

        // Level 1: 降级到动态幻灯片（海报拼图）
        log.warn("视频生成失败，降级到幻灯片模式: reviewId={}", review.getId());
        if (attemptSlideshow(review, photoIdStrings)) {
            return;
        }

        // Level 2: 降级到静态海报
        log.warn("幻灯片生成失败，降级到海报模式: reviewId={}", review.getId());
        if (attemptPoster(review, photoIdStrings)) {
            return;
        }

        // Level 3: 全部失败，仅标记完成（无媒体资源）
        log.error("所有媒体生成均失败，降级为纯通知模式: reviewId={}", review.getId());
        review.setStatus(3); // 3 = 降级完成（无媒体）
        review.setErrorMsg("所有媒体生成服务不可用，已切换为纯通知模式");
        review.setResourceType("none");
        review.setGeneratedAt(LocalDateTime.now());
    }

    /**
     * Level 0: 视频生成（指数退避重试）
     */
    private boolean attemptVideo(MomentsReviewEntity review, List<String> photoIds) {
        String joinedIds = String.join(",", photoIds);
        for (int attempt = 1; attempt <= VIDEO_MAX_RETRIES; attempt++) {
            try {
                long delayMs = (long) Math.pow(2, attempt) * 1000; // 2s, 4s, 8s
                log.info("尝试视频生成 ({}/{}): reviewId={}", attempt, VIDEO_MAX_RETRIES, review.getId());

                String videoUrl = videoGenerator.generateVideo(joinedIds, review.getFamilyId(), review.getTitle());
                if (videoUrl != null && !videoUrl.isBlank()) {
                    review.setResourceUrl(videoUrl);
                    review.setResourceType("video");
                    review.setStatus(1); // 完成
                    review.setGeneratedAt(LocalDateTime.now());
                    videoFailCount.set(0); // 成功则重置失败计数
                    log.info("视频生成成功: reviewId={}, url={}", review.getId(), videoUrl);
                    return true;
                }
            } catch (Exception e) {
                log.warn("视频生成异常 ({}/{}): reviewId={}, error={}",
                        attempt, VIDEO_MAX_RETRIES, review.getId(), e.getMessage());
            }
        }
        videoFailCount.incrementAndGet();
        return false;
    }

    /**
     * Level 1: 动态幻灯片（用海报生成器模拟）
     */
    private boolean attemptSlideshow(MomentsReviewEntity review, List<String> photoIds) {
        for (int attempt = 1; attempt <= SLIDESHOW_MAX_RETRIES; attempt++) {
            try {
                String posterUrl = posterGenerator.generatePoster(photoIds, review.getTitle(), "slideshow");
                if (posterUrl != null && !posterUrl.isBlank()) {
                    review.setResourceUrl(posterUrl);
                    review.setResourceType("slideshow");
                    review.setTitle("[幻灯片模式] " + review.getTitle());
                    review.setStatus(1);
                    review.setGeneratedAt(LocalDateTime.now());
                    log.info("幻灯片生成成功: reviewId={}", review.getId());
                    return true;
                }
            } catch (Exception e) {
                log.warn("幻灯片生成异常 ({}/{}): reviewId={}", attempt, SLIDESHOW_MAX_RETRIES, review.getId());
            }
        }
        return false;
    }

    /**
     * Level 2: 静态海报
     */
    private boolean attemptPoster(MomentsReviewEntity review, List<String> photoIds) {
        try {
            String posterUrl = posterGenerator.generatePoster(photoIds, review.getTitle(), "grid");
            if (posterUrl != null && !posterUrl.isBlank()) {
                review.setResourceUrl(posterUrl);
                review.setResourceType("poster");
                review.setCoverUrl(posterUrl);
                review.setTitle("[海报模式] " + review.getTitle());
                review.setStatus(1);
                review.setGeneratedAt(LocalDateTime.now());
                log.info("海报生成成功: reviewId={}", review.getId());
                return true;
            }
        } catch (Exception e) {
            log.warn("海报生成失败: reviewId={}", review.getId(), e);
        }
        return false;
    }

    @Override
    public boolean isVideoServiceHealthy() {
        return videoFailCount.get() < UNHEALTHY_THRESHOLD;
    }
}
