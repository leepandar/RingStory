package com.ringstory.search.mq;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ringstory.search.entity.PhotoDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * ES 同步消费者
 * 监听 photo_uploaded 事件，将照片数据同步到 ES 索引
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = "photo_uploaded",
        consumerGroup = "es-sync-consumer-group"
)
public class EsSyncConsumer implements RocketMQListener<String> {

    private final ElasticsearchOperations elasticsearchOperations;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onMessage(String message) {
        try {
            JsonNode json = objectMapper.readTree(message);
            Long photoId = json.get("photoId").asLong();
            Long familyId = json.get("familyId").asLong();
            Long uploaderId = json.get("uploaderId").asLong();
            String ossKey = json.get("ossKey").asText();
            String format = json.has("format") ? json.get("format").asText() : null;

            PhotoDocument doc = new PhotoDocument();
            doc.setId(String.valueOf(photoId));
            doc.setPhotoId(photoId);
            doc.setFamilyId(familyId);
            doc.setUploaderId(uploaderId);
            doc.setOssKey(ossKey);
            doc.setShootTime(LocalDateTime.now()); // 后续由 EXIF 数据更新
            doc.setUploadTime(LocalDateTime.now());
            doc.setIsFavorite(false);
            doc.setTags(new ArrayList<>());
            doc.setPersons(new ArrayList<>());

            elasticsearchOperations.save(doc);
            log.info("ES 同步照片成功: photoId={}, familyId={}", photoId, familyId);
        } catch (Exception e) {
            log.error("ES 同步照片失败: message={}", message, e);
        }
    }
}
