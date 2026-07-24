package com.ringstory.notification.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ringstory.notification.entity.NotificationSettingEntity;

/**
 * 通知偏好设置服务接口
 */
public interface NotificationSettingService extends IService<NotificationSettingEntity> {

    /**
     * 获取用户的通知偏好
     */
    NotificationSettingEntity getByUserId(Long userId);

    /**
     * 保存或更新用户通知偏好
     */
    void saveOrUpdateSettings(Long userId, String settingsJson);

    /**
     * 检查用户是否启用了某类型通知
     */
    boolean isNotificationEnabled(Long userId, String notificationType);
}
