package com.ringstory.notification.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ringstory.notification.entity.NotificationEntity;

import java.util.List;

/**
 * 通知服务接口
 */
public interface NotificationService extends IService<NotificationEntity> {

    /**
     * 发送通知
     */
    void sendNotification(Long recipientId, Long familyId, String type,
                           String title, String body, String targetUrl);

    /**
     * 获取未读通知列表
     */
    List<NotificationEntity> getUnread(Long userId);

    /**
     * 标记单条通知已读
     */
    void markAsRead(Long notificationId);

    /**
     * 标记全部通知已读
     */
    void markAllAsRead(Long userId);

    /**
     * 发送通知（带偏好检查）
     * 如果用户关闭了对应类型的通知，则不发送
     */
    void sendNotificationWithPreference(Long recipientId, Long familyId, String type,
                                        String title, String body, String targetUrl);

    /**
     * 获取通知列表（分页）
     */
    List<NotificationEntity> listByUserId(Long userId, int page, int size);

    /**
     * 获取未读通知数量
     */
    long countUnread(Long userId);
}
