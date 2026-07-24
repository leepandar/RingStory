package com.ringstory.common.sms.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 短信服务配置属性
 */
@Data
@ConfigurationProperties(prefix = "ringstory.sms")
public class SmsProperties {

    /**
     * 短信厂商类型：aliyun / tencent
     */
    private String type = "aliyun";

    /**
     * 阿里云短信配置
     */
    private Aliyun aliyun = new Aliyun();

    /**
     * 腾讯云短信配置
     */
    private Tencent tencent = new Tencent();

    @Data
    public static class Aliyun {
        private String accessKeyId;
        private String accessKeySecret;
        private String signName;
        private String templateCode;
    }

    @Data
    public static class Tencent {
        private String secretId;
        private String secretKey;
        private String appId;
        private String signName;
        private String templateId;
    }

}
