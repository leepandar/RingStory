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
     * 触发回顾生成
     */
    @PostMapping("/trigger")
    public R<String> triggerReview(@RequestParam Long familyId,
                                   @RequestParam String type,
                                   @RequestParam int year,
                                   @RequestParam(defaultValue = "1") int monthOrSeason) {
        switch (type) {
            case "monthly" -> reviewService.generateMonthlyReview(familyId, year, monthOrSeason);
            case "seasonal" -> reviewService.generateSeasonalReview(familyId, year, monthOrSeason);
            case "yearly" -> reviewService.generateYearlyReview(familyId, year);
            default -> { }
        }
        return R.success("triggered");
    }
}
