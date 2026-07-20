package com.ringstory.review.entity;
import com.baomidou.mybatisplus.annotation.*; import lombok.Data;
import java.time.LocalDateTime;
@Data @TableName("t_moments_review")
public class MomentsReviewEntity {
    @TableId(type=IdType.AUTO) private Long id; private Long familyId; private String type;
    private String title; private String coverUrl; private String resourceUrl; private String resourceType;
    private String photoIds; private Long starMemberId; private String yearMonth;
    private Integer status; private String errorMsg; private LocalDateTime generatedAt;
    @TableField(fill=FieldFill.INSERT) private LocalDateTime createdAt;
}
