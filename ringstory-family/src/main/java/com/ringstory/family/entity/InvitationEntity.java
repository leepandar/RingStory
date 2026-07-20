package com.ringstory.family.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ringstory.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 邀请实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_invitation")
public class InvitationEntity extends BaseEntity {

    /** 家庭ID */
    private Long familyId;

    /** 邀请者ID */
    private Long inviterId;

    /** 邀请令牌 */
    private String token;

    /** 过期时间 */
    private LocalDateTime expireTime;

    /** 最大使用次数 */
    private Integer maxUses;

    /** 已使用次数 */
    private Integer useCount;

    /** 状态（pending/used/expired） */
    private String status;
}
