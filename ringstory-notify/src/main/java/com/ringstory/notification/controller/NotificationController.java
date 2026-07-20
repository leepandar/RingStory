package com.ringstory.notification.controller;
import com.ringstory.notification.dto.Result;
import com.ringstory.notification.entity.NotificationEntity;
import com.ringstory.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/unread/{userId}")
    public Result<List<NotificationEntity>> getUnread(@PathVariable Long userId) {
        return Result.success(notificationService.getUnread(userId));
    }

    @PostMapping("/{id}/read")
    public Result<?> markRead(@PathVariable Long id) {
        notificationService.markAsRead(id); return Result.success("ok");
    }

    @PostMapping("/read-all/{userId}")
    public Result<?> markAllRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId); return Result.success("ok");
    }
}
