package com.ringstory.notification.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ringstory.notification.entity.NotificationEntity;
import com.ringstory.notification.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 通知服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService extends ServiceImpl<NotificationMapper, NotificationEntity> {

    /**
     * 发送通知
     */
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

    /**
     * 获取未读通知列表
     */
    public List<NotificationEntity> getUnread(Long userId) {
        return lambdaQuery()
                .eq(NotificationEntity::getRecipientId, userId)
                .eq(NotificationEntity::getIsRead, 0)
                .orderByDesc(NotificationEntity::getCreateTime)
                .list();
    }

    /**
     * 标记单条通知已读
     */
    public void markAsRead(Long notificationId) {
        lambdaUpdate()
                .eq(NotificationEntity::getId, notificationId)
                .set(NotificationEntity::getIsRead, 1)
                .update();
    }

    /**
     * 标记全部通知已读
     */
    public void markAllAsRead(Long userId) {
        lambdaUpdate()
                .eq(NotificationEntity::getRecipientId, userId)
                .eq(NotificationEntity::getIsRead, 0)
                .set(NotificationEntity::getIsRead, 1)
                .update();
    }
}
