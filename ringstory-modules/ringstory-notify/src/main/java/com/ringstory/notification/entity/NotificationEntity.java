package com.ringstory.notification.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ringstory.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通知实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_notification")
public class NotificationEntity extends BaseEntity {

    /** 接收者ID */
    private Long recipientId;

    /** 家庭ID */
    private Long familyId;

    /** 通知类型 */
    private String type;

    /** 标题 */
    private String title;

    /** 内容 */
    private String body;

    /** 是否已读（0-否 1-是） */
    private Integer isRead;

    /** 跳转URL */
    private String targetUrl;

    /** 图片URL */
    private String imageUrl;
}
