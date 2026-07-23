package com.ringstory.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ringstory.user.entity.UserEntity;

/**
 * 用户服务接口
 */
public interface UserService extends IService<UserEntity> {

    /**
     * 微信登录
     */
    String wxLogin(String code);

    /**
     * 根据用户名查找用户（管理后台登录）
     */
    UserEntity findByUsername(String username);
}
