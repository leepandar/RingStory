package com.ringstory.notification.mq;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ringstory.notification.service.NotificationTriggerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 通知触发 MQ 消费者
 * 监听各业务事件 Topic，触发通知
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = "notification_trigger",
        consumerGroup = "notification-trigger-consumer-group"
)
public class NotificationTriggerConsumer implements RocketMQListener<String> {

    private final NotificationTriggerService notificationTriggerService;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onMessage(String message) {
        try {
            JsonNode json = objectMapper.readTree(message);
            String eventType = json.get("eventType").asText();
            log.info("收到通知触发事件: type={}, message={}", eventType, message);

            switch (eventType) {
                case "photo_like" -> {
                    Long photoOwnerId = json.get("photoOwnerId").asLong();
                    Long likerId = json.get("likerId").asLong();
                    Long photoId = json.get("photoId").asLong();
                    Long familyId = json.get("familyId").asLong();
                    notificationTriggerService.triggerPhotoLikeNotification(photoOwnerId, likerId, photoId, familyId);
                }
                case "new_comment" -> {
                    Long photoOwnerId = json.get("photoOwnerId").asLong();
                    Long commenterId = json.get("commenterId").asLong();
                    Long photoId = json.get("photoId").asLong();
                    Long familyId = json.get("familyId").asLong();
                    Long replyToUserId = json.has("replyToUserId") && !json.get("replyToUserId").isNull()
                            ? json.get("replyToUserId").asLong() : null;
                    notificationTriggerService.triggerCommentNotification(
                            photoOwnerId, commenterId, photoId, familyId, replyToUserId);
                }
                case "new_member" -> {
                    Long familyId = json.get("familyId").asLong();
                    Long newMemberId = json.get("newMemberId").asLong();
                    List<Long> memberIds = new ArrayList<>();
                    json.get("memberIds").forEach(id -> memberIds.add(id.asLong()));
                    notificationTriggerService.triggerNewMemberNotification(familyId, newMemberId, memberIds);
                }
                case "review_complete" -> {
                    Long familyId = json.get("familyId").asLong();
                    String reviewTitle = json.get("reviewTitle").asText();
                    List<Long> memberIds = new ArrayList<>();
                    json.get("memberIds").forEach(id -> memberIds.add(id.asLong()));
                    notificationTriggerService.triggerReviewCompleteNotification(familyId, memberIds, reviewTitle);
                }
                default -> log.warn("未知的通知触发事件类型: {}", eventType);
            }
        } catch (Exception e) {
            log.error("处理通知触发事件失败: message={}", message, e);
        }
    }
}
