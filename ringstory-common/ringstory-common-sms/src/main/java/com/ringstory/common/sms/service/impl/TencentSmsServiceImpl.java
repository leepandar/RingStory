package com.ringstory.common.sms.service.impl;

import com.ringstory.common.sms.properties.SmsProperties;
import com.ringstory.common.sms.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 腾讯云短信服务实现
 */
@Slf4j
@RequiredArgsConstructor
public class TencentSmsServiceImpl implements SmsService {

    private final SmsProperties smsProperties;

    @Override
    public boolean send(String phone, String templateId, Map<String, String> params) {
        log.info("腾讯云短信发送: phone={}, templateId={}, params={}", phone, templateId, params);

        // TODO: 集成腾讯云短信 SDK
        SmsProperties.Tencent tencent = smsProperties.getTencent();
        log.debug("使用配置: secretId={}, appId={}",
                tencent.getSecretId(), tencent.getAppId());

        // 占位实现，实际需集成腾讯云 SDK
        log.warn("腾讯云短信 SDK 未集成，当前为模拟发送");
        return true;
    }

}
