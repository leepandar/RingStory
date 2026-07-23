package com.ringstory.ringtree.service.impl;

import com.ringstory.ringtree.service.RingTreeInvalidService;
import com.ringstory.ringtree.service.RingTreeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 年轮树缓存失效服务实现
 * 缓存策略：
 * 1. 照片操作后发送 RocketMQ 事件，消费时调用 invalidateCache
 * 2. 设定 TTL 自动过期（30分钟）
 * 3. 使用版本号检测失效（缓存中存储版本号，查询时比对）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RingTreeInvalidServiceImpl implements RingTreeInvalidService {

    private final StringRedisTemplate redisTemplate;
    private final RingTreeService ringTreeService;
    private static final String CACHE_KEY_PREFIX = "ringtree:";
    private static final String VERSION_KEY_PREFIX = "ringtree:version:";
    /** 缓存 TTL：30分钟自动过期 */
    private static final long CACHE_TTL_MINUTES = 30;

    @Override
    public void invalidateCache(Long familyId) {
        String cacheKey = CACHE_KEY_PREFIX + familyId;
        redisTemplate.delete(cacheKey);
        // 递增版本号，使所有持有旧版本的缓存失效
        redisTemplate.opsForValue().increment(VERSION_KEY_PREFIX + familyId);
        log.info("年轮树缓存已失效: familyId={}", familyId);
    }

    @Override
    public void rebuildCache(Long familyId) {
        // 先失效旧缓存
        invalidateCache(familyId);
        // 重新构建（buildTree 会自动回填缓存并设置 TTL）
        ringTreeService.buildTree(familyId);
        log.info("年轮树缓存已重建: familyId={}", familyId);
    }

    /**
     * 获取当前缓存版本号（用于检测缓存是否失效）
     */
    public long getCurrentVersion(Long familyId) {
        String versionStr = redisTemplate.opsForValue().get(VERSION_KEY_PREFIX + familyId);
        return versionStr != null ? Long.parseLong(versionStr) : 0;
    }

    /**
     * 设置缓存 TTL（供 RingTreeServiceImpl 调用）
     */
    public void setCacheTtl(Long familyId) {
        String cacheKey = CACHE_KEY_PREFIX + familyId;
        redisTemplate.expire(cacheKey, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
    }
}
