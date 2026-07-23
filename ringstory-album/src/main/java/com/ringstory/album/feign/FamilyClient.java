package com.ringstory.album.feign;

import com.ringstory.common.response.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 家庭服务 Feign 客户端
 */
@FeignClient(name = "ringstory-family", contextId = "familyClient")
public interface FamilyClient {

    /**
     * 获取家庭信息（用于存储检查）
     */
    @GetMapping("/api/family/{id}")
    R<Map<String, Object>> getFamily(@PathVariable("id") Long id);

    /**
     * 更新家庭存储用量
     */
    @PutMapping("/api/family/{id}/storage")
    R<Void> updateStorage(@PathVariable("id") Long id, @RequestParam("bytes") long bytes);
}
