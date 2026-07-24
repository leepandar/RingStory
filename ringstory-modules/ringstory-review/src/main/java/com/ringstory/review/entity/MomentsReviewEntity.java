package com.ringstory.review.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ringstory.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 放映室回顾实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_moments_review")
public class MomentsReviewEntity extends BaseEntity {

    /** 家庭ID */
    private Long familyId;

    /** 类型（monthly/seasonal/yearly） */
    private String type;

    /** 标题 */
    private String title;

    /** 封面URL */
    private String coverUrl;

    /** 资源URL */
    private String resourceUrl;

    /** 资源类型 */
    private String resourceType;

    /** 时长（秒） */
    private Integer durationSeconds;

    /** 明星成员ID */
    private Long starMemberId;

    /** 年月标识 */
    private String yearMonth;

    /** 状态 */
    private Integer status;

    /** 错误信息 */
    private String errorMsg;

    /** 生成时间 */
    private LocalDateTime generatedAt;
}
