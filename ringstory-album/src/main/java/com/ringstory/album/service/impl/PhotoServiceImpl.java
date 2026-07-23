package com.ringstory.album.service.impl;

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
import com.ringstory.album.mq.NotificationEventProducer;
import com.ringstory.album.mq.PhotoUploadedMessage;
import com.ringstory.album.mq.PhotoUploadedProducer;
import com.ringstory.album.service.PhotoService;
import com.ringstory.album.service.StorageCheckService;
import com.ringstory.album.util.ExifParser;
import com.ringstory.album.util.ImageProcessor;
import com.ringstory.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;

/**
 * 照片服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoServiceImpl extends ServiceImpl<PhotoMapper, PhotoEntity> implements PhotoService {

    private final OSS ossClient;
    private final LikeMapper likeMapper;
    private final CommentMapper commentMapper;
    private final StorageCheckService storageCheckService;
    private final PhotoUploadedProducer photoUploadedProducer;
    private final NotificationEventProducer notificationEventProducer;

    @Value("${aliyun.oss.bucket:ringstory-photos}")
    private String bucket;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PhotoEntity uploadPhoto(Long familyId, Long uploaderId, MultipartFile file) {
        // 1. 计算文件 MD5（用于去重）
        String md5 = ExifParser.computeMd5(file);

        // 2. MD5 去重检查：同一家庭下相同文件不重复上传
        if (md5 != null) {
            PhotoEntity existing = lambdaQuery()
                    .eq(PhotoEntity::getFamilyId, familyId)
                    .eq(PhotoEntity::getMd5, md5)
                    .one();
            if (existing != null) {
                log.info("照片去重命中: familyId={}, md5={}, existingPhotoId={}",
                        familyId, md5, existing.getId());
                return existing;
            }
        }

        // 3. 存储用量检查
        if (!storageCheckService.hasAvailableStorage(familyId, file.getSize())) {
            throw new BusinessException("家庭存储空间已满，请升级套餐或删除旧照片");
        }

        // 4. 解析 EXIF 信息（拍摄时间、GPS、宽高）
        ExifParser.ExifInfo exif = ExifParser.parse(file);

        // 5. 上传到 OSS
        String ossKey = String.format("%d/%s/%s", familyId,
                LocalDateTime.now().toLocalDate(), IdUtil.fastSimpleUUID());
        try (InputStream is = file.getInputStream()) {
            ossClient.putObject(new PutObjectRequest(bucket, ossKey, is));
        } catch (Exception e) {
            throw new BusinessException("OSS 上传失败: " + e.getMessage());
        }

        // 6. 构建照片实体并保存
        PhotoEntity photo = new PhotoEntity();
        photo.setFamilyId(familyId);
        photo.setUploaderId(uploaderId);
        photo.setOssKey(ossKey);
        photo.setOriginalName(file.getOriginalFilename());
        photo.setMd5(md5);
        photo.setFormat(ImageProcessor.detectFormat(file.getOriginalFilename()));
        photo.setFileSize(file.getSize());

        // EXIF 数据：拍摄时间降级为当前时间
        photo.setShootTime(exif.getShootTime() != null ? exif.getShootTime() : LocalDateTime.now());
        photo.setLatitude(exif.getLatitude());
        photo.setLongitude(exif.getLongitude());
        if (exif.getWidth() != null) photo.setWidth(exif.getWidth());
        if (exif.getHeight() != null) photo.setHeight(exif.getHeight());

        photo.setUploadTime(LocalDateTime.now());
        photo.setStatus(2); // 处理中，等待异步管线处理完成后变为正常(1)
        save(photo);

        // 7. 更新家庭存储用量
        storageCheckService.incrementStorageUsed(familyId, file.getSize());

        // 8. 发送 RocketMQ 事件（通知下游：缩略图/内容安全/人脸检测/年轮树/ES同步）
        photoUploadedProducer.send(new PhotoUploadedMessage(
                photo.getId(), familyId, uploaderId, ossKey, md5, photo.getFormat()));

        log.info("照片上传完成: photoId={}, familyId={}, ossKey={}, md5={}, exifShootTime={}",
                photo.getId(), familyId, ossKey, md5, exif.getShootTime());

        return photo;
    }

    @Override
    public List<PhotoEntity> getTimeLine(Long familyId) {
        return baseMapper.selectByFamilyIdOrderByShootTime(familyId);
    }

    @Override
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

        // 触发点赞通知（异步）
        try {
            PhotoEntity photo = getById(photoId);
            if (photo != null) {
                notificationEventProducer.sendPhotoLikeEvent(
                        photo.getUploaderId(), userId, photoId, photo.getFamilyId());
            }
        } catch (Exception e) {
            log.warn("发送点赞通知失败: photoId={}", photoId, e);
        }

        return true;
    }

    @Override
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

        // 触发评论通知（异步）
        try {
            PhotoEntity photo = getById(photoId);
            if (photo != null) {
                notificationEventProducer.sendCommentEvent(
                        photo.getUploaderId(), authorId, photoId, photo.getFamilyId(), null);
            }
        } catch (Exception e) {
            log.warn("发送评论通知失败: photoId={}", photoId, e);
        }

        return comment;
    }
}
