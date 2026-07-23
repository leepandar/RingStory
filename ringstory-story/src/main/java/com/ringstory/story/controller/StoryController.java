package com.ringstory.story.controller;

import com.ringstory.common.response.R;
import com.ringstory.story.entity.PhotoNoteEntity;
import com.ringstory.story.entity.PhotoNoteHistoryEntity;
import com.ringstory.story.service.StoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
        String mentionedUsers = body.getOrDefault("mentionedUsers", "").toString();
        return R.success(storyService.createOrUpdateNote(
                photoId, authorId, content, locationName, mentionedUsers));
    }

    /**
     * 获取笔记版本历史
     */
    @GetMapping("/note/{noteId}/history")
    public R<List<PhotoNoteHistoryEntity>> getNoteHistory(@PathVariable Long noteId) {
        return R.success(storyService.getNoteHistory(noteId));
    }

    /**
     * 回滚笔记到指定版本
     */
    @PostMapping("/note/{noteId}/rollback")
    public R<PhotoNoteEntity> rollbackNote(@PathVariable Long noteId,
                                           @RequestBody Map<String, Object> body) {
        Integer targetVersion = Integer.valueOf(body.get("targetVersion").toString());
        Long operatorId = Long.valueOf(body.get("operatorId").toString());
        return R.success(storyService.rollbackNote(noteId, targetVersion, operatorId));
    }
}
