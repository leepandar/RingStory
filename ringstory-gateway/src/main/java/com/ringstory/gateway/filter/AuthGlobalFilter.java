package com.ringstory.gateway.filter;

import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 网关全局鉴权过滤器
 */
@Slf4j
@Configuration
public class AuthGlobalFilter {

    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                // 拦截所有路由
                .addInclude("/**")
                // 白名单
                .addExclude(
                        "/api/user/wx-login",
                        "/favicon.ico",
                        "/actuator/**"
                )
                // 鉴权逻辑
                .setAuth(obj -> {
                    SaRouter.match("/**").check(r -> StpUtil.checkLogin());
                })
                // 异常处理
                .setError(e -> {
                    log.error("网关鉴权异常: {}", e.getMessage());
                    return SaResult.error(e.getMessage()).setCode(401);
                });
    }
}
