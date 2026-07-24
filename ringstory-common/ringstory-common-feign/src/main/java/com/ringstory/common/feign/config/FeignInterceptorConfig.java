package com.ringstory.common.feign.config;

import com.ringstory.common.feign.interceptor.RingStoryFeignRequestInterceptor;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign 拦截器配置
 * 注册 RingStoryFeignRequestInterceptor 实现 Token 透传和内部调用标记
 */
@Configuration
public class FeignInterceptorConfig {

    @Bean
    public RequestInterceptor ringStoryFeignRequestInterceptor() {
        return new RingStoryFeignRequestInterceptor();
    }

}
