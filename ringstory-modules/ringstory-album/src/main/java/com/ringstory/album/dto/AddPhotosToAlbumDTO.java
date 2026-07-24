package com.ringstory.album.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 添加照片到影集请求
 */
@Data
public class AddPhotosToAlbumDTO {

    /** 操作者ID */
    @NotNull(message = "操作者ID不能为空")
    private Long addedBy;

    /** 照片ID列表 */
    @NotEmpty(message = "照片ID列表不能为空")
    private List<Long> photoIds;
}
