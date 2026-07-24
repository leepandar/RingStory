package com.ringstory.family.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建家庭请求
 */
@Data
public class CreateFamilyDTO {

    /** 家庭名称 */
    @NotBlank(message = "家庭名称不能为空")
    private String name;

    /** 创建者用户ID */
    private Long userId;
}
