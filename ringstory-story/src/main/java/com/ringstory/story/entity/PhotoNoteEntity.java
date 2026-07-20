package com.ringstory.story.entity;
import com.baomidou.mybatisplus.annotation.*; import lombok.Data;
import java.time.LocalDateTime;
@Data @TableName("t_photo_note")
public class PhotoNoteEntity {
    @TableId(type=IdType.AUTO) private Long id; private Long photoId; private Long authorId;
    private String content; private String locationName; private Double latitude; private Double longitude;
    private String mentionedUsers; private Integer version;
    @TableField(fill=FieldFill.INSERT) private LocalDateTime createdAt;
    @TableField(fill=FieldFill.INSERT_UPDATE) private LocalDateTime updatedAt;
    @TableLogic private LocalDateTime deletedAt;
}
