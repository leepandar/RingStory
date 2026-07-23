package com.ringstory.notification.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ringstory.notification.entity.NotificationSettingEntity;
import com.ringstory.notification.mapper.NotificationSettingMapper;
import com.ringstory.notification.service.NotificationSettingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 通知偏好设置服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSettingServiceImpl
        extends ServiceImpl<NotificationSettingMapper, NotificationSettingEntity>
        implements NotificationSettingService {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public NotificationSettingEntity getByUserId(Long userId) {
        return lambdaQuery()
                .eq(NotificationSettingEntity::getUserId, userId)
                .one();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateSettings(Long userId, String settingsJson) {
        NotificationSettingEntity existing = getByUserId(userId);
        if (existing != null) {
            existing.setSettings(settingsJson);
            updateById(existing);
        } else {
            NotificationSettingEntity entity = new NotificationSettingEntity();
            entity.setUserId(userId);
            entity.setSettings(settingsJson);
            save(entity);
        }
    }

    @Override
    public boolean isNotificationEnabled(Long userId, String notificationType) {
        NotificationSettingEntity setting = getByUserId(userId);
        if (setting == null || setting.getSettings() == null) {
            // 没有设置偏好，默认启用所有通知
            return true;
        }
        try {
            JsonNode root = objectMapper.readTree(setting.getSettings());

            // 检查全局静音
            JsonNode muteAll = root.get("mute_all");
            if (muteAll != null && muteAll.asBoolean()) {
                return false;
            }

            // 检查免打扰时段（简化实现：仅判断当前时间是否在时段内）
            JsonNode quietHours = root.get("quiet_hours");
            if (quietHours != null) {
                JsonNode enabled = quietHours.get("enabled");
                if (enabled != null && enabled.asBoolean()) {
                    // TODO: 生产环境应解析 start/end 时间并与当前时间比较
                    // 当前简化处理：免打扰时段内不发送
                    log.debug("用户 {} 处于免打扰时段，跳过 {} 类型通知", userId, notificationType);
                    return false;
                }
            }

            // 检查具体类型开关
            JsonNode typeSetting = root.get(notificationType);
            if (typeSetting != null) {
                JsonNode typeEnabled = typeSetting.get("enabled");
                if (typeEnabled != null) {
                    return typeEnabled.asBoolean();
                }
            }

            // 未配置的类型默认启用
            return true;
        } catch (Exception e) {
            log.warn("解析通知偏好设置失败: userId={}, error={}", userId, e.getMessage());
            return true;
        }
    }
}
