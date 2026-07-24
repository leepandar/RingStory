package com.ringstory.family.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ringstory.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 家庭实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_family")
public class FamilyEntity extends BaseEntity {

    /** 家庭名称 */
    private String name;

    /** 封面URL */
    private String coverUrl;

    /** 创建者ID */
    private Long createdBy;

    /** 加入方式（0-邀请 1-链接） */
    private Integer joinType;

    /** 成员数 */
    private Integer memberCount;

    /** 照片数 */
    private Integer photoCount;

    /** 已用存储(bytes) */
    private Long storageUsed;

    /** 存储上限(bytes) */
    private Long storageLimit;

    /** 是否自动人脸聚类（0-否 1-是） */
    private Integer autoFaceCluster;

    /** 全局隐藏位置（0-否 1-是） */
    private Integer globalHideLocation;

    /** 状态 */
    private Integer status;
}
