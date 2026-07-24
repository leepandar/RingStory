package com.ringstory.album.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ringstory.album.vo.PhotoDetailVO;
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
     * 获取家庭照片时间线（支持时间范围、排序、分页）
     */
    List<PhotoEntity> getTimeLine(Long familyId, String startDate, String endDate,
                                   String sortOrder, int page, int size);

    /**
     * 获取单张照片详情
     */
    PhotoDetailVO getPhotoDetail(Long photoId, Long userId);

    /**
     * 设置点赞/取消点赞
     */
    boolean setLike(Long photoId, Long userId, boolean liked);

    /**
     * 添加评论
     */
    CommentEntity addComment(Long photoId, Long authorId, String content, Long parentId);

    /**
     * 批量为照片添加标签
     */
    void batchAddTags(List<Long> photoIds, List<Long> tagIds);
}
