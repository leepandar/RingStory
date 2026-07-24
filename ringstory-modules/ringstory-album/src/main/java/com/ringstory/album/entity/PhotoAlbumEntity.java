package com.ringstory.album.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ringstory.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 照片影集关联实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_photo_album")
public class PhotoAlbumEntity extends BaseEntity {

    /** 照片ID */
    private Long photoId;

    /** 影集ID */
    private Long albumId;

    /** 排序序号 */
    private Integer sortOrder;

    /** 添加者ID */
    private Long addedBy;
}
