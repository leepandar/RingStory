package com.ringstory.story.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 备注版本历史实体
 */
@Data
@TableName("t_photo_note_history")
public class PhotoNoteHistoryEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 备注ID */
    private Long noteId;

    /** 历史版本内容 */
    private String content;

    /** 版本号 */
    private Integer version;

    /** 编辑者ID */
    private Long editorId;

    /** 创建时间 */
    private LocalDateTime createTime;
}
