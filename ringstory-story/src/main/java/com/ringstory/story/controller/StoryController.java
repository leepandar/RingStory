package com.ringstory.story.controller;
import com.ringstory.story.dto.Result;
import com.ringstory.story.entity.PhotoNoteEntity;
import com.ringstory.story.service.StoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/story")
@RequiredArgsConstructor
public class StoryController {
    private final StoryService storyService;

    @GetMapping("/{photoId}")
    public Result<PhotoNoteEntity> getNote(@PathVariable Long photoId) {
        return Result.success(storyService.lambdaQuery().eq(PhotoNoteEntity::getPhotoId, photoId).one());
    }

    @PostMapping("/{photoId}")
    public Result<PhotoNoteEntity> saveNote(@PathVariable Long photoId, @RequestBody Map<String, Object> body) {
        return Result.success(storyService.createOrUpdateNote(
            photoId,
            Long.valueOf(body.get("authorId").toString()),
            body.getOrDefault("content", "").toString(),
            body.getOrDefault("locationName", "").toString(),
            body.getOrDefault("mentionedUsers", "[]").toString()));
    }
}
