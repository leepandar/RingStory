package com.ringstory.album.entity;
import com.baomidou.mybatisplus.annotation.*; import lombok.Data;
import java.time.LocalDateTime;
@Data @TableName("t_like")
public class LikeEntity {
    @TableId(type=IdType.AUTO) private Long id; private Long photoId; private Long userId;
    private LocalDateTime createdAt;
}
