package com.ringstory.review.service;

import java.util.List;

/**
 * 海报生成服务（Mock）
 * 生成拼图海报作为回顾的降级方案（静态图片替代视频）
 */
public interface PosterGenerator {

    /**
     * 生成拼图海报
     *
     * @param photoUrls 照片 URL 列表
     * @param title     海报标题
     * @param style     海报样式（grid/collage/timeline）
     * @return 生成的海报图片 URL（Mock 返回占位图）
     */
    String generatePoster(List<String> photoUrls, String title, String style);
}
