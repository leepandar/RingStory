package com.ringstory.review.service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ringstory.review.entity.MomentsReviewEntity;
import com.ringstory.review.mapper.MomentsReviewMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService extends ServiceImpl<MomentsReviewMapper, MomentsReviewEntity> {

    public void generateMonthlyReview(Long familyId, int year, int month) {
        String ym = String.format("%d-%02d", year, month);
        log.info("Generating monthly review for family {} - {}", familyId, ym);

        MomentsReviewEntity review = new MomentsReviewEntity();
        review.setFamilyId(familyId);
        review.setType("monthly");
        review.setTitle(year + "年" + month + "月回顾");
        review.setYearMonth(ym);
        review.setStatus(1);
        save(review);
    }

    public void generateSeasonalReview(Long familyId, int year, int season) {
        log.info("Generating seasonal review for family {} - {} Q{}", familyId, year, season);
        MomentsReviewEntity review = new MomentsReviewEntity();
        review.setFamilyId(familyId);
        review.setType("seasonal");
        review.setTitle(year + "年第" + season + "季度回顾");
        review.setYearMonth(String.valueOf(year));
        review.setStatus(1);
        save(review);
    }

    public void generateYearlyReview(Long familyId, int year) {
        log.info("Generating yearly review for family {} - {}", familyId, year);
        MomentsReviewEntity review = new MomentsReviewEntity();
        review.setFamilyId(familyId);
        review.setType("yearly");
        review.setTitle(year + "年度回顾");
        review.setYearMonth(String.valueOf(year));
        review.setStatus(1);
        save(review);
    }

    public List<MomentsReviewEntity> getReviews(Long familyId) {
        return lambdaQuery().eq(MomentsReviewEntity::getFamilyId, familyId)
            .orderByDesc(MomentsReviewEntity::getCreatedAt).list();
    }
}
