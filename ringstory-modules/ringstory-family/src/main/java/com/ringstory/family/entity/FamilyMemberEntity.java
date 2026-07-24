package com.ringstory.family.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ringstory.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 家庭成员实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_family_member")
public class FamilyMemberEntity extends BaseEntity {

    /** 家庭ID */
    private Long familyId;

    /** 用户ID */
    private Long userId;

    /** 角色（admin/member/viewer） */
    private String role;

    /** 别名 */
    private String alias;

    /** 通过哪个邀请加入 */
    private Long joinedVia;

    /** 是否已人脸识别（0-否 1-是） */
    private Integer isFaceRecognized;

    /** 加入时间 */
    private LocalDateTime joinTime;
}
