package com.ringstory.album.service.impl;

import com.ringstory.album.entity.PhotoEntity;
import com.ringstory.album.service.ExportService;
import com.ringstory.album.service.PhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据导出服务实现（Mock）
 * TODO: 生产环境实现完整的 OSS 批量下载 + ZIP 打包
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExportServiceImpl implements ExportService {

    private final PhotoService photoService;

    @Override
    public String exportPhotos(Long familyId, List<Long> photoIds, Long operatorId,
                                String format, Integer compressionLevel) {
        String actualFormat = (format != null) ? format : "zip";
        int actualLevel = (compressionLevel != null) ? compressionLevel : 6;
        log.info("[ExportService] 开始导出照片: familyId={}, photoCount={}, operator={}, format={}, compression={}",
                familyId, photoIds.size(), operatorId, actualFormat, actualLevel);

        // Mock: 返回占位下载链接
        // TODO: 生产环境实现步骤：
        // 1. 根据 photoIds 查询照片的 ossKey
        // 2. 从 OSS 批量下载照片到临时目录
        // 3. 使用 ZipOutputStream 打包为 ZIP
        // 4. 上传 ZIP 到 OSS（设置临时下载链接，有效期 24 小时）
        // 5. 清理临时文件
        // 6. 返回签名下载 URL

        String mockUrl = "https://placeholder.ringstory.com/export/" + familyId + "/photos." + actualFormat;
        log.info("[ExportService] Mock 导出完成: url={}", mockUrl);
        return mockUrl;
    }
}
