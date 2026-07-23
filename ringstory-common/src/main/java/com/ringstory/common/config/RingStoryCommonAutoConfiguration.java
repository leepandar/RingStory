package com.ringstory.common.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * RingStory 公共模块自动配置
 * <p>
 * 通过 Spring Boot 自动配置机制，
 * 使依赖 ringstory-common 的服务自动发现公共组件（Redis、MyBatis-Plus、AOP 等），
 * 无需在各服务启动类中手动指定 scanBasePackages。
 */
@AutoConfiguration
@ComponentScan(basePackages = "com.ringstory.common")
@MapperScan("com.ringstory.common.mapper")
public class RingStoryCommonAutoConfiguration {
}
