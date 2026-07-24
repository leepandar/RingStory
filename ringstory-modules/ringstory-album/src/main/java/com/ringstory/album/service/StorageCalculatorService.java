package com.ringstory.album.service;

import com.ringstory.album.dto.StorageBreakdownDTO;

/**
 * 存储空间计算服务
 * <p>
 * 存储计算公式：
 * 单张照片占用 = originalFileSize + compressedFileSize + thumbnailFileSize
 * 单个视频占用 = originalFileSize + transcodedFileSize(如有)
 * 家庭总用量 = Σ(所有未永久删除的照片/视频占用) + 回收站中30天内的文件
 * <p>
 * HDR/4K 照片规则：
 * - HDR 照片：按原始文件大小计算（不额外乘以系数），压缩版本按实际压缩后大小计算
 * - 4K 照片：同上，按实际存储字节数计算
 * - 原片 + 压缩版 + 缩略图 三份文件均计入存储
 * <p>
 * 删除策略：
 * - 软删除（回收站）：文件仍在 OSS，计入存储用量
 * - 永久删除：从 OSS 移除后释放空间
 * </p>
 */
public interface StorageCalculatorService {

    /**
     * 计算单张照片的存储占用
     *
     * @param photoId 照片ID
     * @return 存储占用字节数（原片 + 压缩 + 缩略图）
     */
    long calculatePhotoStorage(Long photoId);

    /**
     * 获取家庭存储用量明细
     *
     * @param familyId 家庭ID
     * @return 存储明细（按类型分类）
     */
    StorageBreakdownDTO getStorageBreakdown(Long familyId);

    /**
     * 估算上传文件的存储占用（含压缩版本和缩略图）
     *
     * @param originalFileSize 原始文件大小(bytes)
     * @param format           文件格式（jpg/heic/mp4等）
     * @param width            宽度(px)
     * @param height           高度(px)
     * @return 预估总存储占用(bytes)
     */
    long estimateStorageCost(long originalFileSize, String format, int width, int height);
}
