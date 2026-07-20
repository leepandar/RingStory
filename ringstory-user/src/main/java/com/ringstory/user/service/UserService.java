package com.ringstory.user.service;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ringstory.user.entity.UserEntity;
import com.ringstory.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService extends ServiceImpl<UserMapper, UserEntity> {

    public String wxLogin(String code) {
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
        return "jwt_token_" + user.getId();
    }
}
