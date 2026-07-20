package com.ringstory.api.feign;

import com.ringstory.common.response.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserFeignClientFallback implements FallbackFactory<UserFeignClient> {

    @Override
    public UserFeignClient create(Throwable cause) {
        log.error("UserFeignClient fallback: {}", cause.getMessage());
        return new UserFeignClient() {
            @Override
            public R<Object> getUserById(Long id) {
                return R.error("用户服务不可用");
            }

            @Override
            public R<Object> getUserByOpenId(String openId) {
                return R.error("用户服务不可用");
            }

            @Override
            public R<String> wxLogin(String code) {
                return R.error("用户服务不可用");
            }
        };
    }
}
