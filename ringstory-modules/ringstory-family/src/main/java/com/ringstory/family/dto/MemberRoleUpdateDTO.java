package com.ringstory.family.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 成员角色更新请求
 */
@Data
public class MemberRoleUpdateDTO {

    /** 新角色（admin/member/viewer） */
    @NotBlank(message = "角色不能为空")
    private String role;
}
