package com.ringstory.album.controller;
import com.ringstory.album.dto.Result;
import com.ringstory.album.entity.CommentEntity;
import com.ringstory.album.entity.PhotoEntity;
import com.ringstory.album.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/api/album")
@RequiredArgsConstructor
public class PhotoController {
    private final PhotoService photoService;

    @PostMapping("/upload")
    public Result<PhotoEntity> upload(@RequestParam Long familyId, @RequestParam Long userId,
                                       @RequestParam("file") MultipartFile file) {
        return Result.success(photoService.uploadPhoto(familyId, userId, file));
    }

    @GetMapping("/timeline")
    public Result<?> timeline(@RequestParam Long familyId) {
        return Result.success(photoService.getTimeLine(familyId));
    }

    @PostMapping("/{photoId}/like")
    public Result<Boolean> like(@PathVariable Long photoId, @RequestParam Long userId) {
        return Result.success(photoService.toggleLike(photoId, userId));
    }

    @PostMapping("/{photoId}/comment")
    public Result<CommentEntity> comment(@PathVariable Long photoId, @RequestBody Map<String, Object> body) {
        return Result.success(photoService.addComment(
            photoId, Long.valueOf(body.get("authorId").toString()),
            body.get("content").toString(),
            body.containsKey("parentId") ? Long.valueOf(body.get("parentId").toString()) : null));
    }
}
