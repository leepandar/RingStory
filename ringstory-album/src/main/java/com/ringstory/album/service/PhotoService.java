package com.ringstory.album.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ringstory.album.entity.CommentEntity;
import com.ringstory.album.entity.PhotoEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 照片服务接口
 */
public interface PhotoService extends IService<PhotoEntity> {

    /**
     * 上传照片到 OSS 并保存记录
     */
    PhotoEntity uploadPhoto(Long familyId, Long uploaderId, MultipartFile file);

    /**
     * 获取家庭照片时间线
     */
    List<PhotoEntity> getTimeLine(Long familyId);

    /**
     * 点赞/取消点赞
     */
    boolean toggleLike(Long photoId, Long userId);

    /**
     * 添加评论
     */
    CommentEntity addComment(Long photoId, Long authorId, String content, Long parentId);
}
