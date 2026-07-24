package com.ringstory.album.service;

/**
 * 存储用量检查服务接口
 */
public interface StorageCheckService {

    /**
     * 检查家庭存储是否已满
     *
     * @param familyId   家庭ID
     * @param additionalBytes 即将写入的文件大小(bytes)
     * @return true 表示还有空间，false 表示已满
     */
    boolean hasAvailableStorage(Long familyId, long additionalBytes);

    /**
     * 增加家庭已用存储量
     *
     * @param familyId 家庭ID
     * @param bytes    增加的字节数
     */
    void incrementStorageUsed(Long familyId, long bytes);
}
