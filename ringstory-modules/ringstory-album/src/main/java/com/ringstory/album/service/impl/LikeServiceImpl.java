package com.ringstory.album.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ringstory.album.entity.LikeEntity;
import com.ringstory.album.mapper.LikeMapper;
import com.ringstory.album.service.LikeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 点赞服务实现类
 */
@Slf4j
@Service
public class LikeServiceImpl extends ServiceImpl<LikeMapper, LikeEntity> implements LikeService {

    @Override
    public boolean isLiked(Long photoId, Long userId) {
        return lambdaQuery()
                .eq(LikeEntity::getPhotoId, photoId)
                .eq(LikeEntity::getUserId, userId)
                .exists();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeLike(Long photoId, Long userId) {
        lambdaUpdate()
                .eq(LikeEntity::getPhotoId, photoId)
                .eq(LikeEntity::getUserId, userId)
                .setSql("deleted_at = NOW()")
                .update();
    }
}
