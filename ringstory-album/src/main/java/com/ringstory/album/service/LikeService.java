package com.ringstory.album.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ringstory.album.entity.LikeEntity;
import com.ringstory.album.mapper.LikeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 点赞服务
 */
@Slf4j
@Service
public class LikeService extends ServiceImpl<LikeMapper, LikeEntity> {

    /**
     * 检查用户是否已点赞照片
     */
    public boolean isLiked(Long photoId, Long userId) {
        return lambdaQuery()
                .eq(LikeEntity::getPhotoId, photoId)
                .eq(LikeEntity::getUserId, userId)
                .exists();
    }

    /**
     * 取消点赞
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeLike(Long photoId, Long userId) {
        lambdaUpdate()
                .eq(LikeEntity::getPhotoId, photoId)
                .eq(LikeEntity::getUserId, userId)
                .setSql("deleted_at = NOW()")
                .update();
    }
}
