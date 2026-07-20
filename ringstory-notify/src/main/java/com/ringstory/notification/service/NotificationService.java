package com.ringstory.notification.service;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ringstory.notification.entity.NotificationEntity;
import com.ringstory.notification.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService extends ServiceImpl<NotificationMapper, NotificationEntity> {

    public void sendNotification(Long recipientId, Long familyId, String type,
                                  String title, String body, String targetUrl) {
        NotificationEntity n = new NotificationEntity();
        n.setRecipientId(recipientId); n.setFamilyId(familyId); n.setType(type);
        n.setTitle(title); n.setBody(body); n.setTargetUrl(targetUrl);
        n.setIsRead(0);
        save(n);
    }

    public List<NotificationEntity> getUnread(Long userId) {
        return lambdaQuery().eq(NotificationEntity::getRecipientId, userId)
            .eq(NotificationEntity::getIsRead, 0).orderByDesc(NotificationEntity::getCreatedAt).list();
    }

    public void markAsRead(Long notificationId) {
        lambdaUpdate().eq(NotificationEntity::getId, notificationId)
            .set(NotificationEntity::getIsRead, 1).update();
    }

    public void markAllAsRead(Long userId) {
        lambdaUpdate().eq(NotificationEntity::getRecipientId, userId)
            .eq(NotificationEntity::getIsRead, 0).set(NotificationEntity::getIsRead, 1).update();
    }
}
