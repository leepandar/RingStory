package com.ringstory.album.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ringstory.album.entity.LikeEntity;

/**
 * 点赞服务接口
 */
public interface LikeService extends IService<LikeEntity> {

    /**
     * 检查用户是否已点赞照片
     */
    boolean isLiked(Long photoId, Long userId);

    /**
     * 取消点赞
     */
    void removeLike(Long photoId, Long userId);
}
