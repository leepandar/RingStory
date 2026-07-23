package com.ringstory.review.service;

/**
 * 回顾生成服务
 * 负责素材选取算法和回顾生成
 */
public interface ReviewGenerateService {

    /**
     * 为所有家庭生成月度回顾
     */
    void generateMonthlyReviewForAllFamilies(int year, int month);

    /**
     * 为所有家庭生成季度回顾
     */
    void generateSeasonalReviewForAllFamilies(int year, int season);

    /**
     * 为所有家庭生成年度回顾
     */
    void generateYearlyReviewForAllFamilies(int year);
}
