package com.ringstory.story.controller;

import com.ringstory.common.response.R;
import com.ringstory.story.entity.PhotoNoteEntity;
import com.ringstory.story.service.StoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 故事控制器
 */
@RestController
@RequestMapping("/api/story")
@RequiredArgsConstructor
public class StoryController {

    private final StoryService storyService;

    /**
     * 获取照片笔记
     */
    @GetMapping("/{photoId}")
    public R<PhotoNoteEntity> getNote(@PathVariable Long photoId) {
        return R.success(storyService.getNoteByPhotoId(photoId));
    }

    /**
     * 创建或更新照片笔记
     */
    @PostMapping("/{photoId}")
    public R<PhotoNoteEntity> saveNote(@PathVariable Long photoId,
                                       @RequestBody Map<String, Object> body) {
        Long authorId = Long.valueOf(body.get("authorId").toString());
        String content = body.getOrDefault("content", "").toString();
        String locationName = body.getOrDefault("locationName", "").toString();
        String mentionedUsers = body.getOrDefault("mentionedUsers", "[]").toString();
        return R.success(storyService.createOrUpdateNote(
                photoId, authorId, content, locationName, mentionedUsers));
    }
}
