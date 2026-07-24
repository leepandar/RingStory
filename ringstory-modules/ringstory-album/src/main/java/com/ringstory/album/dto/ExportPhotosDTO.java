package com.ringstory.album.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 照片导出请求
 */
@Data
public class ExportPhotosDTO {

    /** 家庭ID */
    @NotNull(message = "家庭ID不能为空")
    private Long familyId;

    /** 操作者ID */
    @NotNull(message = "操作者ID不能为空")
    private Long operatorId;

    /** 照片ID列表 */
    private List<Long> photoIds;

    /** 导出格式（zip/json，默认zip） */
    private String format = "zip";

    /** 压缩级别（0-9，默认6） */
    private Integer compressionLevel = 6;
}
