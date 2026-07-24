package com.ringstory.album.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ringstory.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 照片标签关联实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_photo_tag")
public class PhotoTagEntity extends BaseEntity {

    /** 照片ID */
    private Long photoId;

    /** 标签ID */
    private Long tagId;
}
