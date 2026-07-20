package com.ringstory.family.entity;
import com.baomidou.mybatisplus.annotation.*; import lombok.Data;
import java.time.LocalDateTime;
@Data @TableName("t_invitation")
public class InvitationEntity {
    @TableId(type=IdType.AUTO) private Long id; private Long familyId; private Long inviterId;
    private String token; private LocalDateTime expireTime; private Integer maxUses;
    private Integer useCount; private String status;
    @TableField(fill=FieldFill.INSERT) private LocalDateTime createdAt;
    @TableField(fill=FieldFill.INSERT_UPDATE) private LocalDateTime updatedAt;
}
