package com.ringstory.album.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ringstory.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评论实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_comment")
public class CommentEntity extends BaseEntity {

    /** 照片ID */
    private Long photoId;

    /** 评论者ID */
    private Long authorId;

    /** 父评论ID */
    private Long parentId;

    /** 评论内容 */
    private String content;

    /** 点赞数 */
    private Integer likeCount;
}
