package com.ringstory.review.service.impl;

import com.ringstory.review.service.PosterGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 海报生成服务实现（Mock）
 * TODO: 生产环境使用图片合成库（如 Thumbnailator、Graphics2D）生成拼图海报
 */
@Slf4j
@Service
public class PosterGeneratorImpl implements PosterGenerator {

    @Override
    public String generatePoster(List<String> photoUrls, String title, String style) {
        log.info("[PosterGenerator] Mock 生成海报: title={}, style={}, photoCount={}",
                title, style, photoUrls.size());

        // Mock: 返回一个占位图 URL
        // 生产环境实现步骤：
        // 1. 根据 style 选择布局模板（grid=3x3, collage=自由拼贴, timeline=时间线）
        // 2. 从 OSS 下载照片原图
        // 3. 使用 Graphics2D 或 Thumbnailator 合成海报图片
        // 4. 添加标题文字
        // 5. 上传合成结果到 OSS
        // 6. 返回 OSS URL

        String mockUrl = "https://placeholder.ringstory.com/poster/" + title.hashCode() + ".jpg";
        log.info("[PosterGenerator] Mock 海报生成完成: url={}", mockUrl);
        return mockUrl;
    }
}
