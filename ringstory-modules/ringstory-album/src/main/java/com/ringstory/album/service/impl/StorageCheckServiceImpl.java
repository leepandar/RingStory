package com.ringstory.album.service.impl;

import com.ringstory.album.feign.FamilyClient;
import com.ringstory.album.service.StorageCheckService;
import com.ringstory.common.exception.BusinessException;
import com.ringstory.common.response.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 存储用量检查服务实现
 * <p>
 * 通过 Feign 调用 family-svc 获取家庭存储信息，
 * 上传前检查是否超出限额。
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StorageCheckServiceImpl implements StorageCheckService {

    private final FamilyClient familyClient;

    @Override
    public boolean hasAvailableStorage(Long familyId, long additionalBytes) {
        try {
            R<Map<String, Object>> result = familyClient.getFamily(familyId);
            if (result == null || result.getData() == null) {
                log.warn("获取家庭信息失败，familyId={}，默认放行", familyId);
                return true;
            }
            Map<String, Object> data = result.getData();
            long storageUsed = toLong(data.get("storageUsed"));
            long storageLimit = toLong(data.get("storageLimit"));

            if (storageLimit > 0 && (storageUsed + additionalBytes) > storageLimit) {
                log.info("家庭存储空间已满: familyId={}, used={}, limit={}, additional={}",
                        familyId, storageUsed, storageLimit, additionalBytes);
                return false;
            }
            return true;
        } catch (Exception e) {
            log.warn("存储检查异常，默认放行: familyId={}, error={}", familyId, e.getMessage());
            return true;
        }
    }

    @Override
    public void incrementStorageUsed(Long familyId, long bytes) {
        try {
            familyClient.updateStorage(familyId, bytes);
        } catch (Exception e) {
            log.error("更新存储用量失败: familyId={}, bytes={}, error={}", familyId, bytes, e.getMessage());
        }
    }

    private long toLong(Object value) {
        if (value == null) return 0L;
        if (value instanceof Number) return ((Number) value).longValue();
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
