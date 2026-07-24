package com.ringstory.notification.controller;

import com.ringstory.common.response.R;
import com.ringstory.notification.entity.NotificationSettingEntity;
import com.ringstory.notification.service.NotificationSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 通知偏好设置控制器
 * 路径调整为 /api/notification/settings/{userId}
 */
@RestController
@RequestMapping("/api/notification/settings")
@RequiredArgsConstructor
public class NotificationSettingController {

    private final NotificationSettingService notificationSettingService;

    /**
     * 获取用户通知偏好设置
     */
    @GetMapping("/{userId}")
    public R<NotificationSettingEntity> getSettings(@PathVariable Long userId) {
        NotificationSettingEntity setting = notificationSettingService.getByUserId(userId);
        return R.success(setting);
    }

    /**
     * 保存/更新用户通知偏好设置（PUT 幂等）
     */
    @PutMapping("/{userId}")
    public R<Void> saveSettings(@PathVariable Long userId,
                                @RequestBody String settingsJson) {
        notificationSettingService.saveOrUpdateSettings(userId, settingsJson);
        return R.success();
    }
}
