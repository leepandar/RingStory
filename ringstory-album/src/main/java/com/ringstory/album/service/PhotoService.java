package com.ringstory.album.service;

import cn.hutool.core.util.IdUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ringstory.album.entity.CommentEntity;
import com.ringstory.album.entity.LikeEntity;
import com.ringstory.album.entity.PhotoEntity;
import com.ringstory.album.mapper.CommentMapper;
import com.ringstory.album.mapper.LikeMapper;
import com.ringstory.album.mapper.PhotoMapper;
import com.ringstory.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 照片服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoService extends ServiceImpl<PhotoMapper, PhotoEntity> {

    private final OSS ossClient;
    private final LikeMapper likeMapper;
    private final CommentMapper commentMapper;

    @Value("${aliyun.oss.bucket:ringstory-photos}")
    private String bucket;

    /**
     * 上传照片到 OSS 并保存记录
     */
    @Transactional(rollbackFor = Exception.class)
    public PhotoEntity uploadPhoto(Long familyId, Long uploaderId, MultipartFile file) {
        String ossKey = String.format("%d/%s/%s", familyId,
                LocalDateTime.now().toLocalDate(), IdUtil.fastSimpleUUID());
        try (InputStream is = file.getInputStream()) {
            ossClient.putObject(new PutObjectRequest(bucket, ossKey, is));
        } catch (Exception e) {
            throw new BusinessException("OSS 上传失败: " + e.getMessage());
        }
        PhotoEntity photo = new PhotoEntity();
        photo.setFamilyId(familyId);
        photo.setUploaderId(uploaderId);
        photo.setOssKey(ossKey);
        photo.setOriginalName(file.getOriginalFilename());
        photo.setMd5(IdUtil.fastSimpleUUID());
        photo.setShootTime(LocalDateTime.now());
        photo.setStatus(1);
        save(photo);
        return photo;
    }

    /**
     * 获取家庭照片时间线
     */
    public List<PhotoEntity> getTimeLine(Long familyId) {
        return baseMapper.selectByFamilyIdOrderByShootTime(familyId);
    }

    /**
     * 点赞/取消点赞
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleLike(Long photoId, Long userId) {
        LambdaQueryWrapper<LikeEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LikeEntity::getPhotoId, photoId)
                .eq(LikeEntity::getUserId, userId);
        LikeEntity existing = likeMapper.selectOne(wrapper);
        if (existing != null) {
            likeMapper.deleteById(existing.getId());
            lambdaUpdate().eq(PhotoEntity::getId, photoId)
                    .setSql("like_count = like_count - 1").update();
            return false;
        }
        LikeEntity like = new LikeEntity();
        like.setPhotoId(photoId);
        like.setUserId(userId);
        likeMapper.insert(like);
        lambdaUpdate().eq(PhotoEntity::getId, photoId)
                .setSql("like_count = like_count + 1").update();
        return true;
    }

    /**
     * 添加评论
     */
    @Transactional(rollbackFor = Exception.class)
    public CommentEntity addComment(Long photoId, Long authorId, String content, Long parentId) {
        CommentEntity comment = new CommentEntity();
        comment.setPhotoId(photoId);
        comment.setAuthorId(authorId);
        comment.setContent(content);
        comment.setParentId(parentId);
        commentMapper.insert(comment);
        lambdaUpdate().eq(PhotoEntity::getId, photoId)
                .setSql("comment_count = comment_count + 1").update();
        return comment;
    }
}
