package com.ringstory.album.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 批量添加标签请求
 */
@Data
public class BatchAddTagsDTO {

    /** 照片ID列表 */
    @NotEmpty(message = "照片ID列表不能为空")
    private List<Long> photoIds;

    /** 标签ID列表 */
    @NotEmpty(message = "标签ID列表不能为空")
    private List<Long> tagIds;
}
