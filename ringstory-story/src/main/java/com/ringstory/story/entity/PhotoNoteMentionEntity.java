package com.ringstory.story.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 备注@提及用户关联实体
 */
@Data
@TableName("t_photo_note_mention")
public class PhotoNoteMentionEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 备注ID */
    private Long noteId;

    /** 被@的用户ID */
    private Long userId;

    /** 创建时间 */
    private LocalDateTime createTime;
}
