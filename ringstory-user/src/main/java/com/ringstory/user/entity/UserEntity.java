package com.ringstory.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ringstory.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user")
public class UserEntity extends BaseEntity {

    /** 微信openId */
    private String openId;

    /** 微信unionId */
    private String unionId;

    /** 昵称 */
    private String nickName;

    /** 头像URL */
    private String avatarUrl;

    /** 手机号 */
    private String phone;

    /** 性别（0-未知 1-男 2-女） */
    private Integer gender;

    /** 状态 */
    private Integer status;

    /** 用户偏好设置（JSON） */
    private String preferences;

    /** 最后活跃时间 */
    private LocalDateTime lastActiveTime;
}
