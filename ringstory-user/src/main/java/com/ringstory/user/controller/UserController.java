package com.ringstory.user.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.ringstory.common.log.AccessLog;
import com.ringstory.common.response.R;
import com.ringstory.user.entity.UserEntity;
import com.ringstory.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @AccessLog("微信登录")
    @PostMapping("/wx-login")
    public R<String> wxLogin(@RequestParam String code) {
        return R.success(userService.wxLogin(code));
    }

    @GetMapping("/{id}")
    public R<UserEntity> getUser(@PathVariable Long id) {
        return R.success(userService.getById(id));
    }

    @PutMapping("/{id}/profile")
    public R<Void> updateProfile(@PathVariable Long id, @RequestBody UserEntity user) {
        user.setId(id);
        userService.updateById(user);
        return R.success();
    }

    @GetMapping("/auth/info")
    public R<UserEntity> authInfo() {
        long userId = StpUtil.getLoginIdAsLong();
        return R.success(userService.getById(userId));
    }

    @PostMapping("/auth/logout")
    public R<Void> logout() {
        StpUtil.logout();
        return R.success();
    }
}
package com.ringstory.user.controller;

import com.ringstory.user.entity.UserEntity;
import com.ringstory.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/wx-login")
    public Result<?> wxLogin(@RequestParam String code) {
        return Result.success(userService.wxLogin(code));
    }

    @GetMapping("/{id}")
    public Result<UserEntity> getUser(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    @PutMapping("/{id}/profile")
    public Result<?> updateProfile(@PathVariable Long id, @RequestBody UserEntity user) {
        user.setId(id);
        userService.updateById(user);
        return Result.success();
    }
}
