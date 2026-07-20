package com.ringstory.album.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ringstory.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 照片实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_photo")
public class PhotoEntity extends BaseEntity {

    /** 家庭ID */
    private Long familyId;

    /** 上传者ID */
    private Long uploaderId;

    /** OSS存储键 */
    private String ossKey;

    /** 原始文件名 */
    private String originalName;

    /** 文件MD5 */
    private String md5;

    /** 文件格式 */
    private String format;

    /** 宽度(px) */
    private Integer width;

    /** 高度(px) */
    private Integer height;

    /** 文件大小(bytes) */
    private Long fileSize;

    /** 拍摄时间 */
    private LocalDateTime shootTime;

    /** 上传时间 */
    private LocalDateTime uploadTime;

    /** 纬度 */
    private Double latitude;

    /** 经度 */
    private Double longitude;

    /** 位置名称 */
    private String locationName;

    /** 是否隐藏位置（0-否 1-是） */
    private Integer isHiddenLocation;

    /** 模糊哈希 */
    private String blurHash;

    /** 状态 */
    private Integer status;

    /** 是否收藏（0-否 1-是） */
    private Integer isFavorite;

    /** 点赞数 */
    private Integer likeCount;

    /** 评论数 */
    private Integer commentCount;
}
