package com.ringstory.album.controller;

import com.ringstory.album.service.ExportService;
import com.ringstory.common.response.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
     */
    @PostMapping("/photos")
    public R<Map<String, String>> exportPhotos(@RequestBody Map<String, Object> body) {
        Long familyId = Long.valueOf(body.get("familyId").toString());
        Long operatorId = Long.valueOf(body.get("operatorId").toString());
        @SuppressWarnings("unchecked")
        List<Long> photoIds = ((List<Number>) body.get("photoIds")).stream()
                .map(Number::longValue)
                .toList();

        String downloadUrl = exportService.exportPhotos(familyId, photoIds, operatorId);
        return R.success(Map.of("downloadUrl", downloadUrl));
    }
}
