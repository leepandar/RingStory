package com.ringstory.album.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ringstory.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_album")
public class AlbumEntity extends BaseEntity {

    private Long familyId;
    private String name;
    private Long coverPhotoId;
    private Long creatorId;
    private Integer allowMemberUpload;
    private Integer photoCount;
}
package com.ringstory.album.entity;
import com.baomidou.mybatisplus.annotation.*; import lombok.Data;
import java.time.LocalDateTime;
@Data @TableName("t_album")
public class AlbumEntity {
    @TableId(type=IdType.AUTO) private Long id; private Long familyId; private String name;
    private Long coverPhotoId; private Long creatorId; private Integer allowMemberUpload;
    private Integer photoCount; private LocalDateTime createdAt;
    @TableField(fill=FieldFill.INSERT_UPDATE) private LocalDateTime updatedAt;
    @TableLogic private LocalDateTime deletedAt;
}
