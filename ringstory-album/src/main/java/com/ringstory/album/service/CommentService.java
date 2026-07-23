package com.ringstory.album.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ringstory.album.entity.CommentEntity;

import java.util.List;

/**
 * 评论服务接口
 */
public interface CommentService extends IService<CommentEntity> {

    /**
     * 获取照片下的所有评论（按时间倒序）
     */
    List<CommentEntity> listByPhotoId(Long photoId);

    /**
     * 获取评论的子评论
     */
    List<CommentEntity> listReplies(Long commentId);

    /**
     * 删除评论
     */
    void deleteComment(Long commentId);
}
