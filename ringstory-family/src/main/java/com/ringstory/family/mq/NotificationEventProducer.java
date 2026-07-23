package com.ringstory.family.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通知事件生产者（家庭模块）
 * 发送成员变动事件到 notification_trigger Topic
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventProducer {

    private final RocketMQTemplate rocketMQTemplate;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String TOPIC = "notification_trigger";

    /**
     * 发送新成员加入通知事件
     */
    public void sendNewMemberEvent(Long familyId, Long newMemberId, List<Long> existingMemberIds) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "new_member");
            event.put("familyId", familyId);
            event.put("newMemberId", newMemberId);
            event.put("memberIds", existingMemberIds);
            rocketMQTemplate.convertAndSend(TOPIC, objectMapper.writeValueAsString(event));
            log.debug("发送新成员通知事件: familyId={}, newMemberId={}", familyId, newMemberId);
        } catch (Exception e) {
            log.error("发送新成员通知事件失败", e);
        }
    }
}
