package com.ringstory.common.feign.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Feign 请求拦截器
 * 1. 透传 Authorization 请求头（实现微服务间用户身份传递）
 * 2. 添加 FROM 标记（标识内部调用，目标服务可据此跳过鉴权）
 */
@Slf4j
public class RingStoryFeignRequestInterceptor implements RequestInterceptor {

    private static final String FROM_HEADER = "X-Internal-Call";
    private static final String FROM_IN = "true";

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        // 非 Web 线程（如定时任务、MQ 消费者）直接添加内部调用标记
        if (attributes == null) {
            template.header(FROM_HEADER, FROM_IN);
            return;
        }

        HttpServletRequest request = attributes.getRequest();

        // 透传 Authorization 头
        String authorization = request.getHeader("Authorization");
        if (authorization != null) {
            template.header("Authorization", authorization);
        }

        // 添加内部调用标记
        template.header(FROM_HEADER, FROM_IN);
    }

}
