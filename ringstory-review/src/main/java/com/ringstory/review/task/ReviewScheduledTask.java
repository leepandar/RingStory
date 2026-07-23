package com.ringstory.review.task;

import com.ringstory.review.service.ReviewGenerateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;

/**
 * 回顾定时任务
 * 定时生成月度/季度/年度回顾
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewScheduledTask {

    private final ReviewGenerateService reviewGenerateService;
    private final StringRedisTemplate redisTemplate;
    private static final String LOCK_PREFIX = "review:lock:";

    /**
     * 每月 1 日 08:00 生成月度回顾
     */
    @Scheduled(cron = "0 0 8 1 * ?")
    public void generateMonthlyReview() {
        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        String lockKey = LOCK_PREFIX + "monthly:" + lastMonth;

        if (!tryLock(lockKey, Duration.ofHours(2))) {
            log.info("月度回顾已生成或正在生成，跳过: {}", lastMonth);
            return;
        }

        try {
            log.info("开始生成 {} 月度回顾", lastMonth);
            reviewGenerateService.generateMonthlyReviewForAllFamilies(
                    lastMonth.getYear(), lastMonth.getMonthValue());
        } catch (Exception e) {
            log.error("生成月度回顾失败: {}", lastMonth, e);
            redisTemplate.delete(lockKey);
        }
    }

    /**
     * 每季度首月 1 日 08:30 生成季度回顾
     */
    @Scheduled(cron = "0 30 8 1 1,4,7,10 ?")
    public void generateSeasonalReview() {
        LocalDate lastQuarter = LocalDate.now().minusMonths(3);
        int season = (lastQuarter.getMonthValue() - 1) / 3 + 1;
        String lockKey = LOCK_PREFIX + "seasonal:" + lastQuarter.getYear() + ":" + season;

        if (!tryLock(lockKey, Duration.ofHours(2))) {
            log.info("季度回顾已生成或正在生成，跳过: {} Q{}", lastQuarter.getYear(), season);
            return;
        }

        try {
            log.info("开始生成 {} 年第{}季度回顾", lastQuarter.getYear(), season);
            reviewGenerateService.generateSeasonalReviewForAllFamilies(
                    lastQuarter.getYear(), season);
        } catch (Exception e) {
            log.error("生成季度回顾失败", e);
            redisTemplate.delete(lockKey);
        }
    }

    /**
     * 每年 1 月 1 日 00:10 生成年度回顾
     */
    @Scheduled(cron = "0 10 0 1 1 ?")
    public void generateYearlyReview() {
        int lastYear = LocalDate.now().getYear() - 1;
        String lockKey = LOCK_PREFIX + "yearly:" + lastYear;

        if (!tryLock(lockKey, Duration.ofHours(4))) {
            log.info("年度回顾已生成或正在生成，跳过: {}", lastYear);
            return;
        }

        try {
            log.info("开始生成 {} 年度回顾", lastYear);
            reviewGenerateService.generateYearlyReviewForAllFamilies(lastYear);
        } catch (Exception e) {
            log.error("生成年度回顾失败: {}", lastYear, e);
            redisTemplate.delete(lockKey);
        }
    }

    /**
     * 尝试获取分布式锁
     */
    private boolean tryLock(String key, Duration ttl) {
        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, "locked", ttl);
        return Boolean.TRUE.equals(result);
    }
}
