package com.ringstory.album.mq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 缩略图生成消费者
 * <p>
 * 监听 photo_uploaded 事件，调用 OSS 图片处理管道生成缩略图。
 * 当前为 Mock 实现，仅记录日志。
 * 生产环境应通过 OSS x-oss-process 参数生成缩略图并回写。
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = "photo_uploaded",
        consumerGroup = "thumbnail-consumer-group",
        selectorExpression = "*"
)
public class ThumbnailConsumer implements RocketMQListener<PhotoUploadedMessage> {

    @Override
    public void onMessage(PhotoUploadedMessage message) {
        log.info("[缩略图生成] 收到照片上传事件: photoId={}, ossKey={}",
                message.getPhotoId(), message.getOssKey());

        try {
            // TODO: 生产环境实现
            // 1. 通过 OSS 图片处理管道生成缩略图:
            //    x-oss-process=image/resize,l_360/format,webp/quality,q_80
            // 2. 生成模糊占位图:
            //    x-oss-process=image/resize,w_50/blur,r_50,s_50
            // 3. 将 blur_hash 更新到 photo 表

            log.info("[缩略图生成] 完成: photoId={}", message.getPhotoId());
        } catch (Exception e) {
            log.error("[缩略图生成] 失败: photoId={}, error={}",
                    message.getPhotoId(), e.getMessage());
        }
    }
}
