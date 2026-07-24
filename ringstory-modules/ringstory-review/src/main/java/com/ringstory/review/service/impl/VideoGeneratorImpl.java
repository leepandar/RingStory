package com.ringstory.review.service.impl;

import com.ringstory.review.service.VideoGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 视频生成服务实现（Mock/TODO）
 * TODO: 生产环境使用 FFmpeg 进行视频合成
 */
@Slf4j
@Service
public class VideoGeneratorImpl implements VideoGenerator {

    @Override
    public String generateVideo(String photoIds, Long familyId, String title) {
        log.info("[VideoGenerator] Mock 生成视频: familyId={}, title={}, photoIds={}",
                familyId, title, photoIds);

        // Mock: 返回占位视频 URL
        // 生产环境实现步骤：
        // 1. 根据 photoIds 从 OSS 下载照片
        // 2. 使用 FFmpeg 将照片合成为幻灯片视频（每张 3-5 秒）
        // 3. 添加转场效果（淡入淡出/滑动）
        // 4. 添加背景音乐（从模板库选取）
        // 5. 添加标题字幕
        // 6. 编码输出 MP4（H.264 + AAC）
        // 7. 上传到 OSS，返回 URL

        String mockUrl = "https://placeholder.ringstory.com/video/" + familyId + "/" + title.hashCode() + ".mp4";
        log.info("[VideoGenerator] Mock 视频生成完成: url={}", mockUrl);
        return mockUrl;
    }
}
