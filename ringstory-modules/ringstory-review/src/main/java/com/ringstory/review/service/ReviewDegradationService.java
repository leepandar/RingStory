package com.ringstory.review.service;

import com.ringstory.review.entity.MomentsReviewEntity;

/**
 * 放映室降级策略服务
 * <p>
 * 降级链路：视频 → 动态幻灯片 → 静态海报 → 纯通知
 * 每一级失败自动降级到下一级，并通知用户当前体验模式。
 * </p>
 */
public interface ReviewDegradationService {

    /**
     * 执行降级生成回顾
     * 按优先级依次尝试：视频 → 幻灯片 → 海报 → 纯通知
     *
     * @param review   回顾实体（已创建，status=2 生成中）
     * @param photoIds 照片ID列表
     */
    void generateWithDegradation(MomentsReviewEntity review, java.util.List<Long> photoIds);

    /**
     * 获取当前服务健康状态
     *
     * @return true = 视频生成可用，false = 已降级
     */
    boolean isVideoServiceHealthy();
}
