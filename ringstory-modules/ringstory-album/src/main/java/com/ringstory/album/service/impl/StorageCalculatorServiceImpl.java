package com.ringstory.album.service.impl;

import com.ringstory.album.dto.StorageBreakdownDTO;
import com.ringstory.album.entity.PhotoEntity;
import com.ringstory.album.mapper.PhotoMapper;
import com.ringstory.album.service.StorageCalculatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

/**
 * 存储空间计算服务实现
 * <p>
 * 核心计算公式：
 * ┌───────────────────────────────────────────────────────────────────┐
 * │  单文件存储 = 原片大小 + 压缩版大小 + 缩略图大小                    │
 * │                                                                   │
 * │  家庭总用量 = Σ(照片存储) + Σ(视频存储) + 回收站存储                │
 * │                                                                   │
 * │  压缩版估算 = 原片 × 压缩率(格式相关)                               │
 * │    jpg/jpeg: × 0.5                                                │
 * │    heic:     × 0.4                                                │
 * │    png:      × 0.6                                                │
 * │    raw/dng:  × 0.35                                               │
 * │    其他:     × 0.5                                                │
 * │                                                                   │
 * │  缩略图估算 = 原片 × 0.08 (300px宽, 固定质量)                      │
 * │                                                                   │
 * │  HDR 照片：不额外乘系数，按实际存储字节计算                          │
 * │  4K 照片：同上，按实际文件大小 + 压缩版 + 缩略图计算                  │
 * └───────────────────────────────────────────────────────────────────┘
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StorageCalculatorServiceImpl implements StorageCalculatorService {

    private final PhotoMapper photoMapper;
    private final JdbcTemplate jdbcTemplate;

    /** 各格式压缩率（压缩版 / 原片） */
    private static final Map<String, Double> COMPRESSION_RATIOS = Map.of(
            "jpg", 0.5, "jpeg", 0.5,
            "heic", 0.4, "heif", 0.4,
            "png", 0.6,
            "raw", 0.35, "dng", 0.35, "cr2", 0.35, "nef", 0.35, "arw", 0.35,
            "webp", 0.3,
            "mp4", 1.0, "mov", 1.0  // 视频不压缩
    );
    private static final double DEFAULT_COMPRESSION_RATIO = 0.5;

    /** 缩略图占原片比例 */
    private static final double THUMBNAIL_RATIO = 0.08;

    /** 视频格式集合 */
    private static final Set<String> VIDEO_FORMATS = Set.of("mp4", "mov", "avi", "mkv", "hevc");

    @Override
    public long calculatePhotoStorage(Long photoId) {
        PhotoEntity photo = photoMapper.selectById(photoId);
        if (photo == null) {
            return 0L;
        }
        return calculateSingleFileStorage(photo.getFileSize(), photo.getFormat());
    }

    @Override
    public StorageBreakdownDTO getStorageBreakdown(Long familyId) {
        StorageBreakdownDTO dto = new StorageBreakdownDTO();

        // 查询正常照片的总文件大小
        Map<String, Object> photoStats = jdbcTemplate.queryForMap(
                "SELECT COALESCE(SUM(file_size), 0) as total_size, COUNT(*) as cnt "
                        + "FROM t_photo_2026 WHERE family_id=? AND deleted_at IS NULL AND status = 1",
                familyId);
        long originalBytes = ((Number) photoStats.get("total_size")).longValue();
        long photoCount = ((Number) photoStats.get("cnt")).longValue();

        // 查询视频文件（按格式区分）
        Map<String, Object> videoStats = jdbcTemplate.queryForMap(
                "SELECT COALESCE(SUM(file_size), 0) as total_size, COUNT(*) as cnt "
                        + "FROM t_photo_2026 WHERE family_id=? AND deleted_at IS NULL "
                        + "AND format IN ('mp4','mov','avi','mkv')",
                familyId);
        long videoBytes = ((Number) videoStats.get("total_size")).longValue();
        long videoCount = ((Number) videoStats.get("cnt")).longValue();

        // 查询回收站文件（软删除未超30天）
        Long recycleBinBytes = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(file_size), 0) FROM t_photo_2026 "
                        + "WHERE family_id=? AND deleted_at IS NOT NULL "
                        + "AND deleted_at > DATE_SUB(NOW(), INTERVAL 30 DAY)",
                Long.class, familyId);
        if (recycleBinBytes == null) recycleBinBytes = 0L;

        // 计算压缩版和缩略图占用
        long normalPhotoBytes = originalBytes - videoBytes; // 非视频的原片大小
        long compressedBytes = estimateCompressedSize(normalPhotoBytes, familyId);
        long thumbnailBytes = (long) (normalPhotoBytes * THUMBNAIL_RATIO);
        long recycleCompressed = (long) (recycleBinBytes * DEFAULT_COMPRESSION_RATIO);
        long recycleThumbnail = (long) (recycleBinBytes * THUMBNAIL_RATIO);

        dto.setOriginalBytes(originalBytes);
        dto.setCompressedBytes(compressedBytes + recycleCompressed);
        dto.setThumbnailBytes(thumbnailBytes + recycleThumbnail);
        dto.setVideoBytes(videoBytes);
        dto.setRecycleBinBytes(recycleBinBytes + recycleCompressed + recycleThumbnail);
        dto.setPhotoCount(photoCount);
        dto.setVideoCount(videoCount);
        dto.setTotalBytes(originalBytes + compressedBytes + thumbnailBytes
                + videoBytes + recycleBinBytes + recycleCompressed + recycleThumbnail);

        // 查询限额
        Long storageLimit = jdbcTemplate.queryForObject(
                "SELECT storage_limit FROM t_family WHERE id=?", Long.class, familyId);
        dto.setStorageLimit(storageLimit != null ? storageLimit : 0L);
        dto.setUsagePercent(dto.getStorageLimit() > 0
                ? (double) dto.getTotalBytes() / dto.getStorageLimit() * 100 : 0);

        dto.setFormulaDescription(
                "总用量 = 原片(" + formatSize(originalBytes) + ") + 压缩版(" + formatSize(compressedBytes)
                        + ") + 缩略图(" + formatSize(thumbnailBytes) + ") + 视频(" + formatSize(videoBytes)
                        + ") + 回收站(" + formatSize(recycleBinBytes) + ")");

        return dto;
    }

    @Override
    public long estimateStorageCost(long originalFileSize, String format, int width, int height) {
        return calculateSingleFileStorage(originalFileSize, format);
    }

    /**
     * 计算单个文件的总存储占用（原片 + 压缩 + 缩略图）
     */
    private long calculateSingleFileStorage(long fileSize, String format) {
        if (fileSize <= 0) return 0L;
        String fmt = (format != null) ? format.toLowerCase() : "jpg";

        // 视频不生成压缩版和缩略图（缩略图由视频首帧生成，单独计算）
        if (VIDEO_FORMATS.contains(fmt)) {
            return fileSize;
        }

        double compressionRatio = COMPRESSION_RATIOS.getOrDefault(fmt, DEFAULT_COMPRESSION_RATIO);
        long compressedSize = (long) (fileSize * compressionRatio);
        long thumbnailSize = (long) (fileSize * THUMBNAIL_RATIO);

        return fileSize + compressedSize + thumbnailSize;
    }

    /**
     * 估算家庭照片的压缩版总大小（按格式分布加权）
     */
    private long estimateCompressedSize(long totalOriginalBytes, Long familyId) {
        // 按格式分组查询实际分布
        var formatDist = jdbcTemplate.queryForList(
                "SELECT format, SUM(file_size) as total FROM t_photo_2026 "
                        + "WHERE family_id=? AND deleted_at IS NULL AND status = 1 "
                        + "AND format NOT IN ('mp4','mov','avi','mkv') "
                        + "GROUP BY format",
                familyId);

        if (formatDist.isEmpty()) {
            // 无数据，使用默认压缩率
            return (long) (totalOriginalBytes * DEFAULT_COMPRESSION_RATIO);
        }

        long totalCompressed = 0;
        for (Map<String, Object> row : formatDist) {
            String fmt = String.valueOf(row.get("format")).toLowerCase();
            long fmtTotal = ((Number) row.get("total")).longValue();
            double ratio = COMPRESSION_RATIOS.getOrDefault(fmt, DEFAULT_COMPRESSION_RATIO);
            totalCompressed += (long) (fmtTotal * ratio);
        }
        return totalCompressed;
    }

    /**
     * 格式化文件大小为可读字符串
     */
    private String formatSize(long bytes) {
        if (bytes < 1024) return bytes + "B";
        if (bytes < 1024 * 1024) return String.format("%.1fKB", bytes / 1024.0);
        if (bytes < 1024L * 1024 * 1024) return String.format("%.1fMB", bytes / (1024.0 * 1024));
        return String.format("%.2fGB", bytes / (1024.0 * 1024 * 1024));
    }
}
