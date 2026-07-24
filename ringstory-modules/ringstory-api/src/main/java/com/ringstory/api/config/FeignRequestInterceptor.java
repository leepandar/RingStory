package com.ringstory.api.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Feign 请求拦截器
 * 自动传递 Authorization 等请求头到下游服务
 */
@Configuration
public class FeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        // 传递 Authorization 头（Sa-Token）
        String authorization = request.getHeader("Authorization");
        if (authorization != null) {
            template.header("Authorization", authorization);
        }
        // 传递 X-User-Id（网关注入的用户ID）
        String userId = request.getHeader("X-User-Id");
        if (userId != null) {
            template.header("X-User-Id", userId);
        }
        // 传递 X-Family-Id
        String familyId = request.getHeader("X-Family-Id");
        if (familyId != null) {
            template.header("X-Family-Id", familyId);
        }
    }
}
