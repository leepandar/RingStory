package com.ringstory.album.mq;

import com.ringstory.album.entity.PhotoEntity;
import com.ringstory.album.service.PhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 内容安全检测消费者
 * <p>
 * 监听 photo_uploaded 事件，调用阿里云内容安全（绿网）API 检测图片。
 * 当前为 Mock 实现，默认所有图片通过审核。
 * 生产环境应对接阿里云 Green SDK 进行鉴黄/暴恐/政治敏感检测。
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = "photo_uploaded",
        consumerGroup = "content-safety-consumer-group",
        selectorExpression = "*"
)
public class ContentSafetyConsumer implements RocketMQListener<PhotoUploadedMessage> {

    private final PhotoService photoService;

    @Override
    public void onMessage(PhotoUploadedMessage message) {
        log.info("[内容安全] 开始检测: photoId={}, ossKey={}",
                message.getPhotoId(), message.getOssKey());

        try {
            // TODO: 生产环境实现 - 调用阿里云 Green SDK
            // GreenResult result = greenClient.imageScan()
            //     .withTasks(new ScanTask().setDataId(String.valueOf(message.getPhotoId()))
            //                              .setUrl(ossUrl))
            //     .suggestion(GreenSuggestion.PASS);
            //
            // if (result.getSuggestion() == GreenSuggestion.BLOCK) {
            //     photoService.lambdaUpdate()
            //         .eq(PhotoEntity::getId, message.getPhotoId())
            //         .set(PhotoEntity::getStatus, 3)  // 违规拦截
            //         .update();
            //     // 通知管理员
            //     return;
            // }

            // Mock: 默认通过，标记为正常状态
            photoService.lambdaUpdate()
                    .eq(PhotoEntity::getId, message.getPhotoId())
                    .set(PhotoEntity::getStatus, 1)  // 正常
                    .update();

            log.info("[内容安全] 检测通过: photoId={}", message.getPhotoId());
        } catch (Exception e) {
            log.error("[内容安全] 检测失败: photoId={}, error={}",
                    message.getPhotoId(), e.getMessage());
        }
    }
}
