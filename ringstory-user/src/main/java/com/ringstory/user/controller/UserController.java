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
