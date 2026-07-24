package com.ringstory.album.util;

import lombok.extern.slf4j.Slf4j;

/**
 * 图片处理工具
 * <p>
 * 提供缩略图 URL 生成、压缩参数拼接等功能。
 * 实际图片处理由阿里云 OSS 图片处理管道完成（x-oss-process），
 * 本工具只负责拼接处理参数 URL。
 * </p>
 */
@Slf4j
public class ImageProcessor {

    /**
     * 生成缩略图 URL（通过 OSS 图片处理参数）
     * <p>
     * 等比缩放至长边 360px，输出 WebP 格式，质量 80%
     * </p>
     *
     * @param ossUrl 原始 OSS 图片 URL
     * @return 带处理参数的缩略图 URL
     */
    public static String thumbnailUrl(String ossUrl) {
        if (ossUrl == null || ossUrl.isEmpty()) {
            return ossUrl;
        }
        // OSS 图片处理参数：等比缩放长边360px，WebP格式，质量80%
        String process = "image/resize,l_360/format,webp/quality,q_80";
        String separator = ossUrl.contains("?") ? "&" : "?";
        return ossUrl + separator + "x-oss-process=" + process;
    }

    /**
     * 生成智能压缩图 URL（长边 1080px，质量 80%）
     *
     * @param ossUrl 原始 OSS 图片 URL
     * @return 带处理参数的压缩图 URL
     */
    public static String compressedUrl(String ossUrl) {
        if (ossUrl == null || ossUrl.isEmpty()) {
            return ossUrl;
        }
        String process = "image/resize,l_1080/quality,q_80";
        String separator = ossUrl.contains("?") ? "&" : "?";
        return ossUrl + separator + "x-oss-process=" + process;
    }

    /**
     * 生成模糊占位图 URL（极小尺寸 + 高斯模糊）
     *
     * @param ossUrl 原始 OSS 图片 URL
     * @return 带处理参数的模糊占位图 URL
     */
    public static String blurHashUrl(String ossUrl) {
        if (ossUrl == null || ossUrl.isEmpty()) {
            return ossUrl;
        }
        String process = "image/resize,w_50/blur,r_50,s_50";
        String separator = ossUrl.contains("?") ? "&" : "?";
        return ossUrl + separator + "x-oss-process=" + process;
    }

    /**
     * 获取图片格式后缀
     */
    public static String detectFormat(String originalName) {
        if (originalName == null) return "jpeg";
        String lower = originalName.toLowerCase();
        if (lower.endsWith(".png")) return "png";
        if (lower.endsWith(".heic") || lower.endsWith(".heif")) return "heic";
        if (lower.endsWith(".webp")) return "webp";
        if (lower.endsWith(".gif")) return "gif";
        return "jpeg";
    }
}
