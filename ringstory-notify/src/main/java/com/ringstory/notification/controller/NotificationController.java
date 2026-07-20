package com.ringstory.notification.controller;

import com.ringstory.common.response.R;
import com.ringstory.notification.entity.NotificationEntity;
import com.ringstory.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知控制器
 */
@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 获取未读通知列表
     */
    @GetMapping("/unread/{userId}")
    public R<List<NotificationEntity>> getUnread(@PathVariable Long userId) {
        return R.success(notificationService.getUnread(userId));
    }

    /**
     * 标记单条通知已读
     */
    @PostMapping("/{id}/read")
    public R<Void> markRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return R.success();
    }

    /**
     * 标记全部通知已读
     */
    @PostMapping("/read-all/{userId}")
    public R<Void> markAllRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return R.success();
    }
}
