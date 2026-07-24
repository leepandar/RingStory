package com.ringstory.album.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建标签请求
 */
@Data
public class CreateTagDTO {

    /** 家庭ID */
    @NotNull(message = "家庭ID不能为空")
    private Long familyId;

    /** 标签名称 */
    @NotBlank(message = "标签名称不能为空")
    private String name;

    /** 创建者ID */
    @NotNull(message = "创建者ID不能为空")
    private Long createdBy;
}
