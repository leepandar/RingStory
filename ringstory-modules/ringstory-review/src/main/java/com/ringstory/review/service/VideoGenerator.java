package com.ringstory.review.service;

/**
 * 视频生成服务（Mock/TODO）
 * FFmpeg 视频合成占位
 */
public interface VideoGenerator {

    /**
     * 生成回顾视频
     *
     * @param photoIds  照片 ID 列表（逗号分隔）
     * @param familyId  家庭 ID
     * @param title     视频标题
     * @return 生成的视频 URL（Mock 返回占位 URL）
     */
    String generateVideo(String photoIds, Long familyId, String title);
}
