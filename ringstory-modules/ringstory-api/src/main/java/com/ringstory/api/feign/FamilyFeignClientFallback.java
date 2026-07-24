package com.ringstory.api.feign;

import com.ringstory.common.response.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FamilyFeignClientFallback implements FallbackFactory<FamilyFeignClient> {

    @Override
    public FamilyFeignClient create(Throwable cause) {
        log.error("FamilyFeignClient fallback: {}", cause.getMessage());
        return new FamilyFeignClient() {
            @Override
            public R<Object> getFamilyById(Long id) {
                return R.error("家庭服务不可用");
            }

            @Override
            public R<Object> getMemberRole(Long familyId, Long userId) {
                return R.error("家庭服务不可用");
            }
        };
    }
}
