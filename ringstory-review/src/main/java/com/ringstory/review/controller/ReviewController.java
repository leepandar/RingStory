package com.ringstory.review.controller;
import com.ringstory.review.dto.Result;
import com.ringstory.review.entity.MomentsReviewEntity;
import com.ringstory.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/{familyId}")
    public Result<List<MomentsReviewEntity>> getReviews(@PathVariable Long familyId) {
        return Result.success(reviewService.getReviews(familyId));
    }

    @PostMapping("/trigger")
    public Result<?> triggerReview(@RequestParam Long familyId, @RequestParam String type,
                                    @RequestParam int year, @RequestParam(defaultValue="1") int monthOrSeason) {
        switch (type) {
            case "monthly" -> reviewService.generateMonthlyReview(familyId, year, monthOrSeason);
            case "seasonal" -> reviewService.generateSeasonalReview(familyId, year, monthOrSeason);
            case "yearly" -> reviewService.generateYearlyReview(familyId, year);
        }
        return Result.success("triggered");
    }
}
