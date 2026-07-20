package com.ringstory.album.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ringstory.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 相册实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_album")
public class AlbumEntity extends BaseEntity {

    /** 家庭ID */
    private Long familyId;

    /** 相册名称 */
    private String name;

    /** 封面照片ID */
    private Long coverPhotoId;

    /** 创建者ID */
    private Long creatorId;

    /** 是否允许成员上传（0-否 1-是） */
    private Integer allowMemberUpload;

    /** 照片数量 */
    private Integer photoCount;
}
