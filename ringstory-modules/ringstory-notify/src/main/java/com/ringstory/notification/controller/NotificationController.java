package com.ringstory.notification.controller;

import com.ringstory.common.response.R;
import com.ringstory.notification.entity.NotificationEntity;
import com.ringstory.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 通知控制器
 */
@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 获取未读通知列表（支持限制数量）
     */
    @GetMapping("/unread/{userId}")
    public R<List<NotificationEntity>> getUnread(@PathVariable Long userId,
                                                  @RequestParam(defaultValue = "10") int limit) {
        return R.success(notificationService.getUnread(userId, limit));
    }

    /**
     * 获取通知列表（分页）
     */
    @GetMapping("/list/{userId}")
    public R<List<NotificationEntity>> listNotifications(@PathVariable Long userId,
                                                         @RequestParam(defaultValue = "1") int page,
                                                         @RequestParam(defaultValue = "20") int size) {
        return R.success(notificationService.listByUserId(userId, page, size));
    }

    /**
     * 获取未读通知数量
     */
    @GetMapping("/count/{userId}")
    public R<Map<String, Long>> countUnread(@PathVariable Long userId) {
        return R.success(Map.of("count", notificationService.countUnread(userId)));
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
