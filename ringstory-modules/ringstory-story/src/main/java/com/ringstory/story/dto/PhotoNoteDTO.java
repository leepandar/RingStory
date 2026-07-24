package com.ringstory.story.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 照片笔记创建/更新请求
 */
@Data
public class PhotoNoteDTO {

    /** 笔记内容 */
    private String content;

    /** 位置名称 */
    private String locationName;

    /** 纬度 */
    private Double latitude;

    /** 经度 */
    private Double longitude;

    /** @提及的用户ID列表 */
    private List<Long> mentionedUserIds;

    /** 操作者ID */
    @NotNull(message = "操作者ID不能为空")
    private Long authorId;
}
