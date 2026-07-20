package com.ringstory.family.entity;
import com.baomidou.mybatisplus.annotation.*; import lombok.Data;
import java.time.LocalDateTime;
@Data @TableName("t_family_member")
public class FamilyMemberEntity {
    @TableId(type=IdType.AUTO) private Long id; private Long familyId; private Long userId;
    private String role; private String alias; private Long joinedVia;
    private Integer isFaceRecognized; private Integer status; private LocalDateTime joinTime;
    @TableField(fill=FieldFill.INSERT) private LocalDateTime createdAt;
    @TableField(fill=FieldFill.INSERT_UPDATE) private LocalDateTime updatedAt;
    @TableLogic private LocalDateTime deletedAt;
}
