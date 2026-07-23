package com.ringstory.album.service.impl;

import com.ringstory.album.service.ContentSafetyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 内容安全服务实现（Mock）
 * TODO: 生产环境对接阿里云绿网内容安全 API
 */
@Slf4j
@Service
public class ContentSafetyServiceImpl implements ContentSafetyService {

    @Override
    public boolean checkImage(String ossKey) {
        log.info("[ContentSafety] Mock 图片内容安全检查: ossKey={}", ossKey);
        // Mock: 默认通过
        // TODO: 生产环境实现步骤：
        // 1. 构造阿里云绿网请求（imageScanRequest）
        // 2. 设置检测场景：porn, terrorism, ad
        // 3. 调用 ImageScanRequest 获取结果
        // 4. 解析 suggestion 字段（pass/review/block）
        // 5. block 返回 false，其他返回 true
        return true;
    }

    @Override
    public boolean checkText(String text) {
        log.info("[ContentSafety] Mock 文本内容安全检查: text={}", text);
        // Mock: 默认通过
        // TODO: 生产环境使用 TextScanRequest 检测文本内容
        return true;
    }
}
