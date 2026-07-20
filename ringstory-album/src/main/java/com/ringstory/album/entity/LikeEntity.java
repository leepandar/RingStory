package com.ringstory.album.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ringstory.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 点赞实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_like")
public class LikeEntity extends BaseEntity {

    /** 照片ID */
    private Long photoId;

    /** 用户ID */
    private Long userId;
}
