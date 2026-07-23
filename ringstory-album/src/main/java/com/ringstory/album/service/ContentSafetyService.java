package com.ringstory.album.service;

/**
 * 内容安全服务接口（Mock）
 * 模拟阿里云绿网调用
 */
public interface ContentSafetyService {

    /**
     * 检查图片内容是否安全
     *
     * @param ossKey 图片 OSS Key
     * @return true=安全, false=违规
     */
    boolean checkImage(String ossKey);

    /**
     * 检查文本内容是否安全
     *
     * @param text 文本内容
     * @return true=安全, false=违规
     */
    boolean checkText(String text);
}
