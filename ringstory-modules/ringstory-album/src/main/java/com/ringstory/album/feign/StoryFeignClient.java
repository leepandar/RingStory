package com.ringstory.album.feign;

import com.ringstory.common.response.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 故事模块 Feign 客户端（供照片删除时级联清理笔记数据）
 */
@FeignClient(name = "ringstory-story", contextId = "storyClient")
public interface StoryFeignClient {

    /**
     * 根据照片ID删除笔记及关联数据
     */
    @DeleteMapping("/api/story/internal/note/by-photo/{photoId}")
    R<Void> deleteNoteByPhotoId(@PathVariable("photoId") Long photoId);
}
