package com.ringstory.album.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建相册请求
 */
@Data
public class CreateAlbumDTO {

    /** 家庭ID */
    @NotNull(message = "家庭ID不能为空")
    private Long familyId;

    /** 相册名称 */
    @NotBlank(message = "相册名称不能为空")
    private String name;

    /** 创建者ID */
    @NotNull(message = "创建者ID不能为空")
    private Long creatorId;
}
