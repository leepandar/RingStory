package com.ringstory.family.entity;
import com.baomidou.mybatisplus.annotation.*; import lombok.Data;
import java.time.LocalDateTime;
@Data @TableName("t_family")
public class FamilyEntity {
    @TableId(type=IdType.AUTO) private Long id; private String name; private String coverUrl;
    private Long createdBy; private Integer joinType; private Integer memberCount;
    private Integer photoCount; private Long storageUsed; private Long storageLimit;
    private Integer autoFaceCluster; private Integer globalHideLocation; private Integer status;
    @TableField(fill=FieldFill.INSERT) private LocalDateTime createdAt;
    @TableField(fill=FieldFill.INSERT_UPDATE) private LocalDateTime updatedAt;
    @TableLogic private LocalDateTime deletedAt;
}
