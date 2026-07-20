package com.ringstory.user.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.ringstory.common.log.AccessLog;
import com.ringstory.common.response.R;
import com.ringstory.user.entity.UserEntity;
import com.ringstory.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 微信登录
     */
    @AccessLog("微信登录")
    @PostMapping("/wx-login")
    public R<String> wxLogin(@RequestParam String code) {
        return R.success(userService.wxLogin(code));
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/{id}")
    public R<UserEntity> getUser(@PathVariable Long id) {
        return R.success(userService.getById(id));
    }

    /**
     * 更新个人资料
     */
    @PutMapping("/{id}/profile")
    public R<Void> updateProfile(@PathVariable Long id, @RequestBody UserEntity user) {
        user.setId(id);
        userService.updateById(user);
        return R.success();
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/auth/info")
    public R<UserEntity> authInfo() {
        long userId = StpUtil.getLoginIdAsLong();
        return R.success(userService.getById(userId));
    }

    /**
     * 退出登录
     */
    @PostMapping("/auth/logout")
    public R<Void> logout() {
        StpUtil.logout();
        return R.success();
    }
}
