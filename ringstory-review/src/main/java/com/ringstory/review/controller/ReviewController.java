package com.ringstory.review.controller;

import com.ringstory.common.response.R;
import com.ringstory.review.entity.MomentsReviewEntity;
import com.ringstory.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 放映室控制器
 */
@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 获取家庭回顾列表
     */
    @GetMapping("/{familyId}")
    public R<List<MomentsReviewEntity>> getReviews(@PathVariable Long familyId) {
        return R.success(reviewService.getReviews(familyId));
    }

    /**
     * 触发回顾生成（RESTful 路径参数）
     * POST /api/review/{familyId}/generate?type=monthly&yearMonth=2026-07
     */
    @PostMapping("/{familyId}/generate")
    public R<String> generateReview(@PathVariable Long familyId,
                                     @RequestParam String type,
                                     @RequestParam String yearMonth) {
        // 解析 yearMonth（格式：2026-07）
        String[] parts = yearMonth.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = parts.length > 1 ? Integer.parseInt(parts[1]) : 1;

        switch (type) {
            case "monthly" -> reviewService.generateMonthlyReview(familyId, year, month);
            case "seasonal" -> reviewService.generateSeasonalReview(familyId, year, (month - 1) / 3 + 1);
            case "yearly" -> reviewService.generateYearlyReview(familyId, year);
            default -> throw new IllegalArgumentException("不支持的回顾类型: " + type);
        }
        return R.success("triggered");
    }
}
