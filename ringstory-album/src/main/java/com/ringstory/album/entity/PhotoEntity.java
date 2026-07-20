package com.ringstory.album.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ringstory.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_photo_2026")
public class PhotoEntity extends BaseEntity {

    private Long familyId;
    private Long uploaderId;
    private String ossKey;
    private String originalName;
    private String md5;
    private String format;
    private Integer width;
    private Integer height;
    private Long fileSize;
    private LocalDateTime shootTime;
    private LocalDateTime uploadTime;
    private Double latitude;
    private Double longitude;
    private String locationName;
    private Integer isHiddenLocation;
    private String blurHash;
    private Integer status;
    private Integer isFavorite;
    private Integer likeCount;
    private Integer commentCount;
}
package com.ringstory.album.entity;
import com.baomidou.mybatisplus.annotation.*; import lombok.Data;
import java.time.LocalDateTime;
@Data @TableName("t_photo_2026")
public class PhotoEntity {
    @TableId(type=IdType.AUTO) private Long id; private Long familyId; private Long uploaderId;
    private String ossKey; private String originalName; private String md5; private String format;
    private Integer width; private Integer height; private Long fileSize;
    private LocalDateTime shootTime; private LocalDateTime uploadTime;
    private Double latitude; private Double longitude; private String locationName;
    private Integer isHiddenLocation; private String blurHash; private Integer status;
    private Integer isFavorite; private Integer likeCount; private Integer commentCount;
    @TableField(fill=FieldFill.INSERT) private LocalDateTime createdAt;
    @TableField(fill=FieldFill.INSERT_UPDATE) private LocalDateTime updatedAt;
    @TableLogic private LocalDateTime deletedAt;
}
