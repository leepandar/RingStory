package com.ringstory.album.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ringstory.album.entity.*;
import com.ringstory.album.mapper.*;
import com.ringstory.album.service.PhotoDeleteService;
import com.ringstory.album.feign.StoryFeignClient;
import com.ringstory.common.exception.BusinessException;
import com.ringstory.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 照片删除服务实现（含级联规则）
 * 删除顺序：
 * 1. 检查照片是否在已生成回顾中（提示用户）
 * 2. 删除 t_photo_note + t_photo_note_history + t_photo_note_mention
 * 3. 删除 t_face_photo
 * 4. 删除 t_comment
 * 5. 删除 t_like
 * 6. 删除 t_photo_tag
 * 7. 删除 t_photo_album
 * 8. 软删除 t_photo 本身
 * 9. 发送事件（缓存失效、计数更新）
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoDeleteServiceImpl implements PhotoDeleteService {

    private final PhotoMapper photoMapper;
    private final CommentMapper commentMapper;
    private final LikeMapper likeMapper;
    private final PhotoTagMapper photoTagMapper;
    private final PhotoAlbumMapper photoAlbumMapper;
    private final FacePhotoMapper facePhotoMapper;
    private final StoryFeignClient storyFeignClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> batchDeletePhotos(List<Long> photoIds, Long userId) {
        if (photoIds == null || photoIds.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_PARAMS, "照片ID列表不能为空");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        List<Long> deletedIds = new ArrayList<>();
        List<Long> inReviewIds = new ArrayList<>();

        for (Long photoId : photoIds) {
            PhotoEntity photo = photoMapper.selectById(photoId);
            if (photo == null) {
                log.warn("照片不存在，跳过: photoId={}", photoId);
                continue;
            }

            // 1. 检查是否在已生成回顾中（仅记录，不阻止删除）
            // TODO: 查询 t_review_photo 表，若在回顾中则加入 inReviewIds
            // 当前为预留逻辑，待 review 模块实现后补充

            // 2. 删除照片笔记及相关数据（通过 Feign 调用 story 模块）
            storyFeignClient.deleteNoteByPhotoId(photoId);

            // 3. 删除人脸标注
            deleteFacePhotos(photoId);

            // 4. 删除评论
            deleteComments(photoId);

            // 5. 删除点赞
            deleteLikes(photoId);

            // 6. 删除标签关联
            deletePhotoTags(photoId);

            // 7. 删除影集关联
            deletePhotoAlbums(photoId);

            // 8. 软删除照片本身（MyBatis-Plus @TableLogic 自动处理）
            photoMapper.deleteById(photoId);

            deletedIds.add(photoId);
            log.info("照片级联删除完成: photoId={}, familyId={}", photoId, photo.getFamilyId());
        }

        // 9. TODO: 发送 RocketMQ 事件（缓存失效、计数更新）
        // photoDeleteEventProducer.send(new PhotoDeletedEvent(deletedIds, photo.getFamilyId()));

        result.put("deletedCount", deletedIds.size());
        result.put("deletedIds", deletedIds);
        result.put("inReviewIds", inReviewIds);
        return result;
    }

    private void deleteFacePhotos(Long photoId) {
        LambdaQueryWrapper<FacePhotoEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FacePhotoEntity::getPhotoId, photoId);
        facePhotoMapper.delete(wrapper);
        log.debug("删除人脸标注: photoId={}", photoId);
    }

    private void deleteComments(Long photoId) {
        LambdaQueryWrapper<CommentEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentEntity::getPhotoId, photoId);
        commentMapper.delete(wrapper);
        log.debug("删除评论: photoId={}", photoId);
    }

    private void deleteLikes(Long photoId) {
        LambdaQueryWrapper<LikeEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LikeEntity::getPhotoId, photoId);
        likeMapper.delete(wrapper);
        log.debug("删除点赞: photoId={}", photoId);
    }

    private void deletePhotoTags(Long photoId) {
        LambdaQueryWrapper<PhotoTagEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PhotoTagEntity::getPhotoId, photoId);
        photoTagMapper.delete(wrapper);
        log.debug("删除标签关联: photoId={}", photoId);
    }

    private void deletePhotoAlbums(Long photoId) {
        LambdaQueryWrapper<PhotoAlbumEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PhotoAlbumEntity::getPhotoId, photoId);
        photoAlbumMapper.delete(wrapper);
        log.debug("删除影集关联: photoId={}", photoId);
    }
}
