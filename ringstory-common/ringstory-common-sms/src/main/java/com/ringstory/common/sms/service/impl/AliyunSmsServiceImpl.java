package com.ringstory.common.sms.service.impl;

import com.ringstory.common.sms.properties.SmsProperties;
import com.ringstory.common.sms.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 阿里云短信服务实现
 */
@Slf4j
@RequiredArgsConstructor
public class AliyunSmsServiceImpl implements SmsService {

    private final SmsProperties smsProperties;

    @Override
    public boolean send(String phone, String templateId, Map<String, String> params) {
        log.info("阿里云短信发送: phone={}, templateId={}, params={}", phone, templateId, params);

        // TODO: 集成阿里云短信 SDK
        SmsProperties.Aliyun aliyun = smsProperties.getAliyun();
        log.debug("使用配置: accessKeyId={}, signName={}",
                aliyun.getAccessKeyId(), aliyun.getSignName());

        // 占位实现，实际需集成阿里云 SDK
        log.warn("阿里云短信 SDK 未集成，当前为模拟发送");
        return true;
    }

}
