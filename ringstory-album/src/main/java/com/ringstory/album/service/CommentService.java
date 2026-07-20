package com.ringstory.album.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ringstory.album.entity.CommentEntity;
import com.ringstory.album.mapper.CommentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 评论服务
 */
@Slf4j
@Service
public class CommentService extends ServiceImpl<CommentMapper, CommentEntity> {

    /**
     * 获取照片下的所有评论（按时间倒序）
     */
    public List<CommentEntity> listByPhotoId(Long photoId) {
        return lambdaQuery()
                .eq(CommentEntity::getPhotoId, photoId)
                .orderByDesc(CommentEntity::getCreateTime)
                .list();
    }

    /**
     * 获取评论的子评论
     */
    public List<CommentEntity> listReplies(Long commentId) {
        return lambdaQuery()
                .eq(CommentEntity::getParentId, commentId)
                .orderByAsc(CommentEntity::getCreateTime)
                .list();
    }

    /**
     * 删除评论
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long commentId) {
        removeById(commentId);
    }
}
