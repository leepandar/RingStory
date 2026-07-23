package com.ringstory.ringtree.service.impl;

import com.ringstory.ringtree.service.RingTreeInvalidService;
import com.ringstory.ringtree.service.RingTreeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 年轮树缓存失效服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RingTreeInvalidServiceImpl implements RingTreeInvalidService {

    private final StringRedisTemplate redisTemplate;
    private final RingTreeService ringTreeService;
    private static final String CACHE_KEY_PREFIX = "ringtree:";

    @Override
    public void invalidateCache(Long familyId) {
        String cacheKey = CACHE_KEY_PREFIX + familyId;
        redisTemplate.delete(cacheKey);
        log.info("年轮树缓存已失效: familyId={}", familyId);
    }

    @Override
    public void rebuildCache(Long familyId) {
        // 先失效旧缓存
        invalidateCache(familyId);
        // 重新构建（buildTree 会自动回填缓存）
        ringTreeService.buildTree(familyId);
        log.info("年轮树缓存已重建: familyId={}", familyId);
    }
}
