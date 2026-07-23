package com.ringstory.album.mq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 人脸检测消费者
 * <p>
 * 监听 photo_uploaded 事件，调用 Rust face-svc 进行人脸检测和特征提取。
 * 当前为 Mock 实现，仅记录日志。
 * 生产环境应通过 Feign 调用 face-svc 的 /api/face/detect 接口。
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = "photo_uploaded",
        consumerGroup = "face-detect-consumer-group",
        selectorExpression = "*"
)
public class FaceDetectConsumer implements RocketMQListener<PhotoUploadedMessage> {

    @Override
    public void onMessage(PhotoUploadedMessage message) {
        log.info("[人脸检测] 收到检测任务: photoId={}, familyId={}, ossKey={}",
                message.getPhotoId(), message.getFamilyId(), message.getOssKey());

        try {
            // TODO: 生产环境实现
            // 1. 通过 Feign 调用 face-svc:
            //    POST http://ringstory-face:8090/api/face/detect
            //    Body: { "image_url": ossUrl, "family_id": familyId }
            //
            // 2. 接收返回的人脸列表:
            //    faces: [{ x, y, w, h, confidence, embedding(512维) }]
            //
            // 3. 将人脸 embedding 写入 ES (dense_vector)
            //
            // 4. 执行增量聚类:
            //    - 计算与已有 cluster 的 cosine 相似度
            //    - 若 >= 0.65 则归入该 cluster
            //    - 否则创建新 cluster
            //
            // 5. 更新 t_face_cluster 和 t_face_photo 表

            log.info("[人脸检测] 完成: photoId={}", message.getPhotoId());
        } catch (Exception e) {
            log.error("[人脸检测] 失败: photoId={}, error={}",
                    message.getPhotoId(), e.getMessage());
        }
    }
}
