package com.ringstory.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("t_user")
public class UserEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String openId;
    private String unionId;
    private String nickName;
    private String avatarUrl;
    private String phone;
    private Integer gender;
    private Integer status;
    private String preferences;
    private LocalDateTime lastActiveTime;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    @TableLogic
    private LocalDateTime deletedAt;
}
