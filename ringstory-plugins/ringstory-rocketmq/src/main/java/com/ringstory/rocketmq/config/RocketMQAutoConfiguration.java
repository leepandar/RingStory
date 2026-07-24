package com.ringstory.rocketmq.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

/**
 * RocketMQ Starter 自动配置
 * <p>
 * 引入 ringstory-rocketmq 依赖后，Spring Boot 自动加载本配置类，
 * 注册生产者服务和消费者相关组件 Bean。
 */
@AutoConfiguration
@EnableConfigurationProperties(RocketMQProperties.class)
@ComponentScan(basePackages = "com.ringstory.rocketmq")
public class RocketMQAutoConfiguration {

}
