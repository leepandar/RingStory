package com.ringstory.album.service;

import java.util.List;

/**
 * 数据导出服务接口
 */
public interface ExportService {

    /**
     * 批量导出照片（打包 ZIP）
     *
     * @param familyId  家庭 ID
     * @param photoIds  照片 ID 列表
     * @param operatorId 操作者 ID
     * @return 导出文件下载 URL
     */
    String exportPhotos(Long familyId, List<Long> photoIds, Long operatorId);
}
