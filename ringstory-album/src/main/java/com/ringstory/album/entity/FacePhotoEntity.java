package com.ringstory.album.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ringstory.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 人脸-照片关联实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_face_photo")
public class FacePhotoEntity extends BaseEntity {

    /** 人脸聚类ID */
    private Long faceClusterId;

    /** 照片ID */
    private Long photoId;

    /** 人脸坐标 JSON */
    private String faceCoords;

    /** 置信度 */
    private Double confidence;

    /** 特征向量（用于二次聚类/比对） */
    private String embedding;

    /** 是否已排除（0-否 1-是） */
    private Integer isExcluded;
}
