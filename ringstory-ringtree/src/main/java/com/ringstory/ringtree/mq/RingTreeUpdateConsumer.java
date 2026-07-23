package com.ringstory.ringtree.mq;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ringstory.ringtree.service.RingTreeInvalidService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 年轮树增量更新消费者
 * 监听 photo_uploaded 事件，使对应家庭的缓存失效
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = "photo_uploaded",
        consumerGroup = "ringtree-update-consumer-group"
)
public class RingTreeUpdateConsumer implements RocketMQListener<String> {

    private final RingTreeInvalidService ringTreeInvalidService;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onMessage(String message) {
        try {
            JsonNode json = objectMapper.readTree(message);
            Long familyId = json.get("familyId").asLong();
            log.info("收到照片上传事件，失效年轮树缓存: familyId={}", familyId);
            ringTreeInvalidService.invalidateCache(familyId);
        } catch (Exception e) {
            log.error("处理年轮树增量更新失败: message={}", message, e);
        }
    }
}
