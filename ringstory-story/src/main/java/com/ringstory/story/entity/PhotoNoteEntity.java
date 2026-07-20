package com.ringstory.story.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.ringstory.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 照片笔记实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_photo_note")
public class PhotoNoteEntity extends BaseEntity {

    /** 照片ID */
    private Long photoId;

    /** 作者ID */
    private Long authorId;

    /** 笔记内容 */
    private String content;

    /** 位置名称 */
    private String locationName;

    /** 纬度 */
    private Double latitude;

    /** 经度 */
    private Double longitude;

    /** @提及的用户ID列表 */
    private String mentionedUsers;
}
