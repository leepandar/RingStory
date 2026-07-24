package com.ringstory.notification.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ringstory.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通知偏好设置实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_notification_setting")
public class NotificationSettingEntity extends BaseEntity {

    /** 用户ID */
    private Long userId;

    /** 通知偏好JSON */
    private String settings;
}
