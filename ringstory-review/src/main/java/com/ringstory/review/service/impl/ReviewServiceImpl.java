package com.ringstory.review.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ringstory.review.entity.MomentsReviewEntity;
import com.ringstory.review.mapper.MomentsReviewMapper;
import com.ringstory.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 放映室服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl extends ServiceImpl<MomentsReviewMapper, MomentsReviewEntity> implements ReviewService {

    @Override
    public void generateMonthlyReview(Long familyId, int year, int month) {
        String ym = String.format("%d-%02d", year, month);
        log.info("生成月度回顾: familyId={}, ym={}", familyId, ym);

        MomentsReviewEntity review = new MomentsReviewEntity();
        review.setFamilyId(familyId);
        review.setType("monthly");
        review.setTitle(year + "年" + month + "月回顾");
        review.setYearMonth(ym);
        review.setStatus(1);
        save(review);
    }

    @Override
    public void generateSeasonalReview(Long familyId, int year, int season) {
        log.info("生成季度回顾: familyId={}, year={}, quarter={}", familyId, year, season);
        MomentsReviewEntity review = new MomentsReviewEntity();
        review.setFamilyId(familyId);
        review.setType("seasonal");
        review.setTitle(year + "年第" + season + "季度回顾");
        review.setYearMonth(String.valueOf(year));
        review.setStatus(1);
        save(review);
    }

    @Override
    public void generateYearlyReview(Long familyId, int year) {
        log.info("生成年度回顾: familyId={}, year={}", familyId, year);
        MomentsReviewEntity review = new MomentsReviewEntity();
        review.setFamilyId(familyId);
        review.setType("yearly");
        review.setTitle(year + "年度回顾");
        review.setYearMonth(String.valueOf(year));
        review.setStatus(1);
        save(review);
    }

    @Override
    public List<MomentsReviewEntity> getReviews(Long familyId) {
        return lambdaQuery().eq(MomentsReviewEntity::getFamilyId, familyId)
                .orderByDesc(MomentsReviewEntity::getCreateTime).list();
    }
}
