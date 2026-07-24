package com.ringstory.common.sms.config;

import com.ringstory.common.sms.service.impl.AliyunSmsServiceImpl;
import com.ringstory.common.sms.service.impl.TencentSmsServiceImpl;
import com.ringstory.common.sms.properties.SmsProperties;
import com.ringstory.common.sms.service.SmsService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 短信服务自动配置
 * 根据配置选择对应的短信厂商实现
 */
@Configuration
@EnableConfigurationProperties(SmsProperties.class)
public class SmsAutoConfiguration {

    @Bean
    @ConditionalOnProperty(name = "ringstory.sms.type", havingValue = "aliyun", matchIfMissing = true)
    public SmsService aliyunSmsService(SmsProperties smsProperties) {
        return new AliyunSmsServiceImpl(smsProperties);
    }

    @Bean
    @ConditionalOnProperty(name = "ringstory.sms.type", havingValue = "tencent")
    public SmsService tencentSmsService(SmsProperties smsProperties) {
        return new TencentSmsServiceImpl(smsProperties);
    }

}
