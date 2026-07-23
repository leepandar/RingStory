package com.ringstory.album.mq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * 照片上传事件生产者
 * <p>
 * 照片写入数据库后，发送 photo_uploaded 事件通知下游服务。
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PhotoUploadedProducer {

    private final RocketMQTemplate rocketMQTemplate;

    public static final String TOPIC = "photo_uploaded";

    /**
     * 发送照片上传完成事件
     */
    public void send(PhotoUploadedMessage message) {
        try {
            rocketMQTemplate.send(TOPIC, MessageBuilder.withPayload(message).build());
            log.info("发送 photo_uploaded 事件: photoId={}, familyId={}",
                    message.getPhotoId(), message.getFamilyId());
        } catch (Exception e) {
            log.error("发送 photo_uploaded 事件失败: photoId={}, error={}",
                    message.getPhotoId(), e.getMessage());
        }
    }
}
