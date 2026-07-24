package com.ringstory.album.dto;

import lombok.Data;

/**
 * 家庭存储用量明细
 * <p>
 * 计算公式说明：
 * - totalBytes = originalBytes + compressedBytes + thumbnailBytes + videoBytes + recycleBinBytes
 * - originalBytes: 原片（含 HDR/4K）实际占用字节
 * - compressedBytes: 压缩版本占用字节（WebP 转换后，约为原片的 30-50%）
 * - thumbnailBytes: 缩略图占用字节（固定 300px 宽，约为原片的 5-10%）
 * - videoBytes: 视频文件占用字节
 * - recycleBinBytes: 回收站中未永久删除的文件占用（30天后自动清理）
 * </p>
 */
@Data
public class StorageBreakdownDTO {

    /** 总存储用量(bytes) */
    private long totalBytes;

    /** 原片存储(bytes) - 含 HDR/4K 原文件 */
    private long originalBytes;

    /** 压缩版本存储(bytes) - WebP 压缩后 */
    private long compressedBytes;

    /** 缩略图存储(bytes) */
    private long thumbnailBytes;

    /** 视频文件存储(bytes) */
    private long videoBytes;

    /** 回收站存储(bytes) - 30天内软删除的文件 */
    private long recycleBinBytes;

    /** 照片数量 */
    private long photoCount;

    /** 视频数量 */
    private long videoCount;

    /** 存储限额(bytes) */
    private long storageLimit;

    /** 使用百分比 */
    private double usagePercent;

    /**
     * 计算公式说明（返回给客户端展示）
     */
    private String formulaDescription;
}
