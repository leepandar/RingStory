package com.ringstory.review.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ringstory.review.entity.MomentsReviewEntity;

import java.util.List;

/**
 * 放映室服务接口
 */
public interface ReviewService extends IService<MomentsReviewEntity> {

    /**
     * 生成月度回顾
     */
    void generateMonthlyReview(Long familyId, int year, int month);

    /**
     * 生成季度回顾
     */
    void generateSeasonalReview(Long familyId, int year, int season);

    /**
     * 生成年度回顾
     */
    void generateYearlyReview(Long familyId, int year);

    /**
     * 获取家庭回顾列表
     */
    List<MomentsReviewEntity> getReviews(Long familyId);
}
