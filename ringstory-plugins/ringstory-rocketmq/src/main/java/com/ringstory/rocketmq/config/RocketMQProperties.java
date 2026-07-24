package com.ringstory.rocketmq.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RocketMQ 配置属性
 * <p>
 * 通过 application.yml 中 ringstory.rocketmq 前缀读取配置。
 */
@Data
@ConfigurationProperties(prefix = "ringstory.rocketmq")
public class RocketMQProperties {

    /**
     * 是否启用 RocketMQ 自动配置，默认 true
     */
    private boolean enabled = true;

    /**
     * 生产者配置
     */
    private Producer producer = new Producer();

    /**
     * 消费者配置
     */
    private Consumer consumer = new Consumer();

    @Data
    public static class Producer {
        /**
         * 发送超时时间（毫秒），默认 3000
         */
        private int sendTimeout = 3000;

        /**
         * 发送失败重试次数，默认 2
         */
        private int retryTimes = 2;

        /**
         * 消息发送失败后是否同步抛异常，默认 true
         */
        private boolean failFast = true;
    }

    @Data
    public static class Consumer {
        /**
         * 消费线程数，默认 20
         */
        private int threadNum = 20;

        /**
         * 消费失败最大重试次数，默认 3
         */
        private int maxRetryTimes = 3;

        /**
         * 消费超时时间（毫秒），默认 15000
         */
        private int consumeTimeout = 15000;
    }
}
