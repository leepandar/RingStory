package com.ringstory.notification.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ringstory.notification.entity.NotificationEntity;
import com.ringstory.notification.mapper.NotificationMapper;
import com.ringstory.notification.service.NotificationService;
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
}
