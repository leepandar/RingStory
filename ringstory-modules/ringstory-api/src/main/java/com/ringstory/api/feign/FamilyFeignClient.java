package com.ringstory.api.feign;

import com.ringstory.common.response.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ringstory-family", fallbackFactory = FamilyFeignClientFallback.class)
public interface FamilyFeignClient {

    @GetMapping("/api/family/{id}")
    R<Object> getFamilyById(@PathVariable("id") Long id);

    @GetMapping("/api/family/member/{familyId}/role/{userId}")
    R<Object> getMemberRole(@PathVariable("familyId") Long familyId, @PathVariable("userId") Long userId);
}
