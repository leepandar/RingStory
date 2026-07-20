package com.ringstory.album.entity;
import com.baomidou.mybatisplus.annotation.*; import lombok.Data;
import java.time.LocalDateTime;
@Data @TableName("t_comment")
public class CommentEntity {
    @TableId(type=IdType.AUTO) private Long id; private Long photoId; private Long authorId;
    private Long parentId; private String content; private Integer likeCount;
    private LocalDateTime createdAt; @TableLogic private LocalDateTime deletedAt;
}
