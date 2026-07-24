package com.ringstory.album.controller;

import com.ringstory.album.dto.ExportPhotosDTO;
import com.ringstory.album.service.ExportService;
import com.ringstory.common.response.R;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 数据导出控制器
 */
@RestController
@RequestMapping("/api/album/export")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    /**
     * 批量导出照片
     * 支持指定导出格式（zip/json）和压缩级别（0-9）
     */
    @PostMapping("/photos")
    public R<String> exportPhotos(@Valid @RequestBody ExportPhotosDTO request) {
        String downloadUrl = exportService.exportPhotos(
                request.getFamilyId(), request.getPhotoIds(), request.getOperatorId(),
                request.getFormat(), request.getCompressionLevel());
        return R.success(downloadUrl);
    }
}
