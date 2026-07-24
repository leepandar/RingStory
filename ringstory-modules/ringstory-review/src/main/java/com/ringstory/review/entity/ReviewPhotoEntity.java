package com.ringstory.review.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 回顾照片关联实体
 */
@Data
@TableName("t_review_photo")
public class ReviewPhotoEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 回顾ID */
    private Long reviewId;

    /** 照片ID */
    private Long photoId;

    /** 排序序号 */
    private Integer sortOrder;

    /** 创建时间 */
    private LocalDateTime createTime;
}
