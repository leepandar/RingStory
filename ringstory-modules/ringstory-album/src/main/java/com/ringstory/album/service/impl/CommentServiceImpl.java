package com.ringstory.album.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ringstory.album.entity.CommentEntity;
import com.ringstory.album.mapper.CommentMapper;
import com.ringstory.album.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 评论服务实现类
 */
@Slf4j
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, CommentEntity> implements CommentService {

    @Override
    public List<CommentEntity> listByPhotoId(Long photoId) {
        return lambdaQuery()
                .eq(CommentEntity::getPhotoId, photoId)
                .orderByDesc(CommentEntity::getCreateTime)
                .list();
    }

    @Override
    public List<CommentEntity> listByPhotoIdPaged(Long photoId, int page, int size) {
        Page<CommentEntity> pageParam = new Page<>(page, size);
        Page<CommentEntity> result = lambdaQuery()
                .eq(CommentEntity::getPhotoId, photoId)
                .orderByDesc(CommentEntity::getCreateTime)
                .page(pageParam);
        return result.getRecords();
    }

    @Override
    public List<CommentEntity> listReplies(Long commentId) {
        return lambdaQuery()
                .eq(CommentEntity::getParentId, commentId)
                .orderByAsc(CommentEntity::getCreateTime)
                .list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId) {
        removeById(commentId);
    }
}
