package com.ringstory.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 微信订阅消息推送服务（Mock 占位）
 * TODO: 生产环境对接微信 subscribeMessage.send API
 */
@Slf4j
@Service
public class WxSubscribeService {

    /**
     * 发送微信订阅消息
     *
     * @param userId     接收者用户ID
     * @param templateId 消息模板ID
     * @param data       模板数据 JSON
     * @param page       跳转页面路径
     */
    public void sendSubscribeMessage(Long userId, String templateId, String data, String page) {
        // Mock 实现：仅记录日志
        log.info("[WxSubscribeService] Mock 发送微信订阅消息: userId={}, templateId={}, data={}, page={}",
                userId, templateId, data, page);
        // TODO: 生产环境实现步骤：
        // 1. 通过 userId 查询用户的微信 openId
        // 2. 获取 access_token（调用微信 API）
        // 3. 构造请求体（touser, template_id, page, data, miniprogram_state）
        // 4. POST https://api.weixin.qq.com/cgi-bin/message/subscribe/send
        // 5. 处理返回结果（errcode=0 表示成功）
    }

    /**
     * 发送通知的辅助方法：根据通知类型选择模板
     */
    public void sendByNotificationType(Long userId, String notificationType, String title, String body) {
        String templateId = getTemplateId(notificationType);
        if (templateId == null) {
            log.debug("通知类型 {} 没有对应的微信模板，跳过推送", notificationType);
            return;
        }
        String data = String.format("{\"thing1\":{\"value\":\"%s\"},\"thing2\":{\"value\":\"%s\"}}",
                truncate(title, 20), truncate(body, 20));
        sendSubscribeMessage(userId, templateId, data, "/pages/notification/index");
    }

    private String getTemplateId(String type) {
        // Mock: 返回模拟模板ID，生产环境应从配置中读取
        return switch (type) {
            case "photo_like" -> "TEMPLATE_LIKE_001";
            case "new_comment" -> "TEMPLATE_COMMENT_001";
            case "new_member" -> "TEMPLATE_MEMBER_001";
            case "review_complete" -> "TEMPLATE_REVIEW_001";
            default -> null;
        };
    }

    private String truncate(String text, int maxLen) {
        if (text == null) return "";
        return text.length() > maxLen ? text.substring(0, maxLen) + "..." : text;
    }
}
