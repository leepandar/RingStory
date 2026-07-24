package com.ringstory.rocketmq.producer;

import com.ringstory.rocketmq.config.RocketMQProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;

/**
 * RocketMQ 生产者自动配置
 * <p>
 * 当 ringstory.rocketmq.enabled=true（默认）时，
 * 自动注册 RocketMQProducerService Bean 供业务模块注入使用。
 */
@AutoConfiguration
@ConditionalOnProperty(prefix = "ringstory.rocketmq", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RocketMQProducerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RocketMQProducerService rocketMQProducerService(
            StreamBridge streamBridge,
            RocketMQProperties properties) {
        return new RocketMQProducerService(streamBridge, properties);
    }
}
