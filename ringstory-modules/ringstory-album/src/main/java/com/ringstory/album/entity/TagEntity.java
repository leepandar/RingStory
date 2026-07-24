package com.ringstory.album.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ringstory.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 标签实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_tag")
public class TagEntity extends BaseEntity {

    /** 家庭ID */
    private Long familyId;

    /** 标签名称 */
    private String name;

    /** 创建者ID */
    private Long createdBy;
}
