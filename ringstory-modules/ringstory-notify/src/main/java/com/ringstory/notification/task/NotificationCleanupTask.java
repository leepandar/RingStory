package com.ringstory.notification.task;

import com.ringstory.notification.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 通知清理定时任务
 * 清理 30 天前的已读通知
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationCleanupTask {

    private final NotificationMapper notificationMapper;

    /**
     * 每天凌晨 3:00 清理 30 天前的已读通知
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanupOldNotifications() {
        log.info("开始清理过期通知...");
        try {
            int deleted = notificationMapper.delete(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<
                            com.ringstory.notification.entity.NotificationEntity>()
                            .eq(com.ringstory.notification.entity.NotificationEntity::getIsRead, 1)
                            .lt(com.ringstory.notification.entity.NotificationEntity::getCreateTime,
                                    java.time.LocalDateTime.now().minusDays(30)));
            log.info("通知清理完成: 删除 {} 条过期已读通知", deleted);
        } catch (Exception e) {
            log.error("通知清理失败", e);
        }
    }
}
