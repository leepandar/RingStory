package com.ringstory.album.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ringstory.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 人脸聚类实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_face_cluster")
public class FaceClusterEntity extends BaseEntity {

    /** 家庭ID */
    private Long familyId;

    /** 聚类名称（用户可自定义） */
    private String name;

    /** 封面照片ID */
    private Long coverPhotoId;

    /** 状态（0-未命名 1-已命名） */
    private Integer status;

    /** 照片数量 */
    private Integer photoCount;
}
