package com.ringstory.user.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ringstory.user.entity.UserEntity;
import com.ringstory.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService extends ServiceImpl<UserMapper, UserEntity> {

    /**
     * 微信登录
     */
    @Transactional(rollbackFor = Exception.class)
    public String wxLogin(String code) {
        // TODO: 调用微信API获取真实openId
        String openId = "mock_" + RandomUtil.randomString(16);
        UserEntity user = lambdaQuery().eq(UserEntity::getOpenId, openId).one();
        if (user == null) {
            user = new UserEntity();
            user.setOpenId(openId);
            user.setNickName("用户" + RandomUtil.randomNumbers(6));
            user.setStatus(1);
            user.setLastActiveTime(LocalDateTime.now());
            save(user);
        } else {
            user.setLastActiveTime(LocalDateTime.now());
            updateById(user);
        }
        // 使用 Sa-Token 登录
        StpUtil.login(user.getId());
        return StpUtil.getTokenValue();
    }
}
