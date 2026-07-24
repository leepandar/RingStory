package com.ringstory.common.sms.service;

import java.util.Map;

/**
 * 短信服务接口
 * 提供统一的短信发送能力
 */
public interface SmsService {

    /**
     * 发送短信
     *
     * @param phone      手机号
     * @param templateId 模板 ID
     * @param params     模板参数
     * @return 是否发送成功
     */
    boolean send(String phone, String templateId, Map<String, String> params);

}
