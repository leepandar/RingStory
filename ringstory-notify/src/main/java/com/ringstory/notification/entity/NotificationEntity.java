package com.ringstory.notification.entity;
import com.baomidou.mybatisplus.annotation.*; import lombok.Data;
import java.time.LocalDateTime;
@Data @TableName("t_notification")
public class NotificationEntity {
    @TableId(type=IdType.AUTO) private Long id; private Long recipientId; private Long familyId;
    private String type; private String title; private String body;
    private Integer isRead; private String targetUrl; private String imageUrl;
    @TableField(fill=FieldFill.INSERT) private LocalDateTime createdAt;
}
