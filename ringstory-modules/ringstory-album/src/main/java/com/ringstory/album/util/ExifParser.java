package com.ringstory.album.util;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * EXIF 元数据解析工具
 * <p>
 * 从图片文件中提取拍摄时间、GPS 坐标、宽高、格式等信息。
 * 若无法读取 EXIF，降级使用文件修改时间或当前时间。
 * </p>
 */
@Slf4j
public class ExifParser {

    /**
     * 解析图片 EXIF 信息
     */
    public static ExifInfo parse(MultipartFile file) {
        ExifInfo info = new ExifInfo();
        try (InputStream is = new BufferedInputStream(file.getInputStream())) {
            Metadata metadata = ImageMetadataReader.readMetadata(is);

            // 1. 拍摄时间
            ExifSubIFDDirectory subIFD = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (subIFD != null) {
                Date dateOriginal = subIFD.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
                if (dateOriginal != null) {
                    info.setShootTime(LocalDateTime.ofInstant(dateOriginal.toInstant(), ZoneId.systemDefault()));
                }
            }
            // 降级：ExifIFD0
            if (info.getShootTime() == null) {
                ExifIFD0Directory ifd0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
                if (ifd0 != null) {
                    Date dateModified = ifd0.getDate(ExifIFD0Directory.TAG_DATETIME);
                    if (dateModified != null) {
                        info.setShootTime(LocalDateTime.ofInstant(dateModified.toInstant(), ZoneId.systemDefault()));
                    }
                }
            }

            // 2. GPS 坐标
            GpsDirectory gpsDir = metadata.getFirstDirectoryOfType(GpsDirectory.class);
            if (gpsDir != null && gpsDir.getGeoLocation() != null) {
                var geo = gpsDir.getGeoLocation();
                if (!geo.isZero()) {
                    info.setLatitude(geo.getLatitude());
                    info.setLongitude(geo.getLongitude());
                }
            }

            // 3. 图片宽高
            if (subIFD != null) {
                Integer width = subIFD.getInteger(ExifSubIFDDirectory.TAG_EXIF_IMAGE_WIDTH);
                Integer height = subIFD.getInteger(ExifSubIFDDirectory.TAG_EXIF_IMAGE_HEIGHT);
                if (width != null && width > 0) info.setWidth(width);
                if (height != null && height > 0) info.setHeight(height);
            }

            log.debug("EXIF 解析完成: shootTime={}, lat={}, lng={}, w={}, h={}",
                    info.getShootTime(), info.getLatitude(), info.getLongitude(),
                    info.getWidth(), info.getHeight());

        } catch (Exception e) {
            log.warn("EXIF 解析失败，使用降级值: {}", e.getMessage());
        }
        return info;
    }

    /**
     * 计算文件 MD5
     */
    public static String computeMd5(MultipartFile file) {
        try {
            return cn.hutool.crypto.digest.DigestUtil.md5Hex(file.getInputStream());
        } catch (Exception e) {
            log.warn("MD5 计算失败: {}", e.getMessage());
            return null;
        }
    }

    @Data
    public static class ExifInfo {
        private LocalDateTime shootTime;
        private Double latitude;
        private Double longitude;
        private Integer width;
        private Integer height;
    }
}
