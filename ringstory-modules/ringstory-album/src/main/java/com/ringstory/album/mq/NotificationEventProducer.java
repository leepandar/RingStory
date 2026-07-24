package com.ringstory.album.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 通知事件生产者
 * 发送业务事件到 notification_trigger Topic，由通知服务消费并触发通知
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventProducer {

    private final RocketMQTemplate rocketMQTemplate;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String TOPIC = "notification_trigger";

    /**
     * 发送点赞通知事件
     */
    public void sendPhotoLikeEvent(Long photoOwnerId, Long likerId, Long photoId, Long familyId) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "photo_like");
            event.put("photoOwnerId", photoOwnerId);
            event.put("likerId", likerId);
            event.put("photoId", photoId);
            event.put("familyId", familyId);
            rocketMQTemplate.convertAndSend(TOPIC, objectMapper.writeValueAsString(event));
            log.debug("发送点赞通知事件: photoOwnerId={}, likerId={}, photoId={}", photoOwnerId, likerId, photoId);
        } catch (Exception e) {
            log.error("发送点赞通知事件失败", e);
        }
    }

    /**
     * 发送评论通知事件
     */
    public void sendCommentEvent(Long photoOwnerId, Long commenterId, Long photoId,
                                  Long familyId, Long replyToUserId) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "new_comment");
            event.put("photoOwnerId", photoOwnerId);
            event.put("commenterId", commenterId);
            event.put("photoId", photoId);
            event.put("familyId", familyId);
            if (replyToUserId != null) {
                event.put("replyToUserId", replyToUserId);
            }
            rocketMQTemplate.convertAndSend(TOPIC, objectMapper.writeValueAsString(event));
            log.debug("发送评论通知事件: photoOwnerId={}, commenterId={}, photoId={}", photoOwnerId, commenterId, photoId);
        } catch (Exception e) {
            log.error("发送评论通知事件失败", e);
        }
    }
}
