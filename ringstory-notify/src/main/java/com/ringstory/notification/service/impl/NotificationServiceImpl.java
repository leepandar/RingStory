package com.ringstory.notification.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ringstory.notification.entity.NotificationEntity;
import com.ringstory.notification.mapper.NotificationMapper;
import com.ringstory.notification.service.NotificationService;
import com.ringstory.notification.service.NotificationSettingService;
import com.ringstory.notification.service.WxSubscribeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 通知服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, NotificationEntity> implements NotificationService {

    private final NotificationSettingService notificationSettingService;
    private final WxSubscribeService wxSubscribeService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendNotification(Long recipientId, Long familyId, String type,
                                  String title, String body, String targetUrl) {
        NotificationEntity n = new NotificationEntity();
        n.setRecipientId(recipientId);
        n.setFamilyId(familyId);
        n.setType(type);
        n.setTitle(title);
        n.setBody(body);
        n.setTargetUrl(targetUrl);
        n.setIsRead(0);
        save(n);
    }

    @Override
    public List<NotificationEntity> getUnread(Long userId) {
        return lambdaQuery()
                .eq(NotificationEntity::getRecipientId, userId)
                .eq(NotificationEntity::getIsRead, 0)
                .orderByDesc(NotificationEntity::getCreateTime)
                .list();
    }

    @Override
    public void markAsRead(Long notificationId) {
        lambdaUpdate()
                .eq(NotificationEntity::getId, notificationId)
                .set(NotificationEntity::getIsRead, 1)
                .update();
    }

    @Override
    public void markAllAsRead(Long userId) {
        lambdaUpdate()
                .eq(NotificationEntity::getRecipientId, userId)
                .eq(NotificationEntity::getIsRead, 0)
                .set(NotificationEntity::getIsRead, 1)
                .update();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendNotificationWithPreference(Long recipientId, Long familyId, String type,
                                                String title, String body, String targetUrl) {
        // 检查用户通知偏好
        if (!notificationSettingService.isNotificationEnabled(recipientId, type)) {
            log.info("用户 {} 已关闭 {} 类型通知，跳过发送", recipientId, type);
            return;
        }
        // 发送站内通知
        sendNotification(recipientId, familyId, type, title, body, targetUrl);
        // 尝试推送微信订阅消息
        wxSubscribeService.sendByNotificationType(recipientId, type, title, body);
    }

    @Override
    public List<NotificationEntity> listByUserId(Long userId, int page, int size) {
        Page<NotificationEntity> pageParam = new Page<>(page, size);
        Page<NotificationEntity> result = lambdaQuery()
                .eq(NotificationEntity::getRecipientId, userId)
                .orderByDesc(NotificationEntity::getCreateTime)
                .page(pageParam);
        return result.getRecords();
    }

    @Override
    public long countUnread(Long userId) {
        return lambdaQuery()
                .eq(NotificationEntity::getRecipientId, userId)
                .eq(NotificationEntity::getIsRead, 0)
                .count();
    }
}
