package com.ringstory.album.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 评论创建请求
 */
@Data
public class CommentDTO {

    /** 评论者ID */
    @NotNull(message = "评论者ID不能为空")
    private Long authorId;

    /** 评论内容 */
    @NotBlank(message = "评论内容不能为空")
    private String content;

    /** 父评论ID（NULL为顶级评论） */
    private Long parentId;
}
