package com.ringstory.story.controller;

import com.ringstory.common.response.R;
import com.ringstory.story.dto.PhotoNoteDTO;
import com.ringstory.story.entity.PhotoNoteEntity;
import com.ringstory.story.entity.PhotoNoteHistoryEntity;
import com.ringstory.story.service.StoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * 创建或更新照片笔记（标准 DTO + @用户解析）
     */
    @PostMapping("/{photoId}")
    public R<PhotoNoteEntity> saveNote(@PathVariable Long photoId,
                                       @Valid @RequestBody PhotoNoteDTO request) {
        return R.success(storyService.createOrUpdateNote(
                photoId, request.getAuthorId(), request.getContent(),
                request.getLocationName(), request.getMentionedUserIds()));
    }

    /**
     * 获取笔记版本历史（分页）
     */
    @GetMapping("/note/{noteId}/history")
    public R<List<PhotoNoteHistoryEntity>> getNoteHistory(@PathVariable Long noteId,
                                                           @RequestParam(defaultValue = "1") int page,
                                                           @RequestParam(defaultValue = "20") int size) {
        return R.success(storyService.getNoteHistory(noteId, page, size));
    }

    /**
     * 回滚笔记到指定版本
     */
    @PostMapping("/note/{noteId}/rollback")
    public R<PhotoNoteEntity> rollbackNote(@PathVariable Long noteId,
                                           @RequestBody java.util.Map<String, Object> body) {
        Integer targetVersion = Integer.valueOf(body.get("targetVersion").toString());
        Long operatorId = Long.valueOf(body.get("operatorId").toString());
        return R.success(storyService.rollbackNote(noteId, targetVersion, operatorId));
    }

    /**
     * 根据照片ID删除笔记及关联数据（供 album 模块内部调用）
     */
    @DeleteMapping("/internal/note/by-photo/{photoId}")
    public R<Void> deleteNoteByPhotoId(@PathVariable Long photoId) {
        storyService.deleteNoteByPhotoId(photoId);
        return R.success();
    }
}
