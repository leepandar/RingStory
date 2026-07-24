package com.ringstory.common.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * RingStory MyBatis 模块自动配置
 * <p>
 * 自动扫描 MyBatis-Plus 配置、审计日志 Mapper 等组件
 */
@AutoConfiguration
@ComponentScan(basePackages = {"com.ringstory.common.config", "com.ringstory.common.aspect", "com.ringstory.common.exception"})
@MapperScan("com.ringstory.common.mapper")
public class RingStoryMyBatisAutoConfiguration {
}
