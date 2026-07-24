package com.ringstory.album.service;

import java.util.List;

/**
 * 数据导出服务接口
 */
public interface ExportService {

    /**
     * 批量导出照片
     *
     * @param familyId         家庭 ID
     * @param photoIds         照片 ID 列表
     * @param operatorId       操作者 ID
     * @param format           导出格式（zip/json）
     * @param compressionLevel 压缩级别（0-9）
     * @return 导出文件下载 URL
     */
    String exportPhotos(Long familyId, List<Long> photoIds, Long operatorId,
                        String format, Integer compressionLevel);
}
