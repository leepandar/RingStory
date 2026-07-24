package com.ringstory.api.feign;

import com.ringstory.common.response.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "ringstory-user", fallbackFactory = UserFeignClientFallback.class)
public interface UserFeignClient {

    @GetMapping("/api/user/{id}")
    R<Object> getUserById(@PathVariable("id") Long id);

    @GetMapping("/api/user/openid/{openId}")
    R<Object> getUserByOpenId(@PathVariable("openId") String openId);

    @PostMapping("/api/user/wx-login")
    R<String> wxLogin(@RequestParam("code") String code);
}
