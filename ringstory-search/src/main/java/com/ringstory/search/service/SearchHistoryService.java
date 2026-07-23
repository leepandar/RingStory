package com.ringstory.search.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 搜索历史记录服务（Redis 存储）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SearchHistoryService {

    private final StringRedisTemplate redisTemplate;
    private static final String HISTORY_KEY_PREFIX = "search:history:";
    /** 保留最近10条记录 */
    private static final int MAX_HISTORY = 10;
    /** 保留30天（通过 Redis TTL 实现） */
    private static final long TTL_DAYS = 30;

    /**
     * 添加搜索记录
     */
    public void addHistory(Long userId, String keyword) {
        if (keyword == null || keyword.isBlank()) return;
        String key = HISTORY_KEY_PREFIX + userId;
        // 先移除相同的（避免重复）
        redisTemplate.opsForList().remove(key, 0, keyword);
        // 添加到头部
        redisTemplate.opsForList().leftPush(key, keyword);
        // 保留最近 MAX_HISTORY 条
        redisTemplate.opsForList().trim(key, 0, MAX_HISTORY - 1);
        // 设置 TTL（30天自动清理）
        redisTemplate.expire(key, TTL_DAYS, java.util.concurrent.TimeUnit.DAYS);
    }

    /**
     * 获取搜索历史
     */
    public List<String> getHistory(Long userId) {
        String key = HISTORY_KEY_PREFIX + userId;
        List<String> range = redisTemplate.opsForList().range(key, 0, MAX_HISTORY - 1);
        return range != null ? range : new ArrayList<>();
    }

    /**
     * 清空搜索历史
     */
    public void clearHistory(Long userId) {
        String key = HISTORY_KEY_PREFIX + userId;
        redisTemplate.delete(key);
    }
}
