package com.ringstory.common.config;

import cn.dev33.satoken.stp.StpInterface;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Sa-Token 权限认证配置
 * 各服务可通过覆盖此 Bean 来实现自定义权限获取逻辑
 */
@Component
public class SaTokenPermissionConfig implements StpInterface {

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 默认返回空权限列表，各服务可按需覆盖
        return new ArrayList<>();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 默认返回空角色列表，各服务可按需覆盖
        return new ArrayList<>();
    }
}
