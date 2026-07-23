package com.ringstory.album.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 照片详情 VO
 */
@Data
public class PhotoDetailVO {

    private Long id;
    private Long familyId;
    private Long uploaderId;
    private String ossKey;
    private String originalName;
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
    private List<String> tags;
    private Boolean likedByCurrentUser;
}
