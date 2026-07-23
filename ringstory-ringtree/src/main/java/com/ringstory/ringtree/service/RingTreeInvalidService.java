package com.ringstory.ringtree.service;

/**
 * 年轮树缓存失效/重建服务
 */
public interface RingTreeInvalidService {

    /**
     * 使缓存失效（新照片上传时调用）
     */
    void invalidateCache(Long familyId);

    /**
     * 强制重建缓存
     */
    void rebuildCache(Long familyId);
}
