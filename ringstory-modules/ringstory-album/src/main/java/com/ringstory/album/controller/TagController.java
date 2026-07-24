package com.ringstory.album.controller;

import com.ringstory.album.dto.AddPhotosToAlbumDTO;
import com.ringstory.album.dto.CreateTagDTO;
import com.ringstory.album.entity.PhotoAlbumEntity;
import com.ringstory.album.entity.TagEntity;
import com.ringstory.album.service.AlbumService;
import com.ringstory.album.service.TagService;
import com.ringstory.common.response.R;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签控制器
 */
@RestController
@RequestMapping("/api/album")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;
    private final AlbumService albumService;

    // ==================== 标签管理 ====================

    /**
     * 创建标签
     */
    @PostMapping("/tag")
    public R<TagEntity> createTag(@Valid @RequestBody CreateTagDTO request) {
        return R.success(tagService.createTag(request.getFamilyId(), request.getName(), request.getCreatedBy()));
    }

    /**
     * 获取家庭所有标签
     */
    @GetMapping("/tag/list")
    public R<List<TagEntity>> listTags(@RequestParam Long familyId) {
        return R.success(tagService.listByFamilyId(familyId));
    }

    /**
     * 删除标签
     */
    @DeleteMapping("/tag/{tagId}")
    public R<Void> deleteTag(@PathVariable Long tagId) {
        tagService.deleteTag(tagId);
        return R.success();
    }

    /**
     * 获取高频标签（推荐）
     */
    @GetMapping("/tag/hot")
    public R<List<TagEntity>> hotTags(@RequestParam Long familyId,
                                      @RequestParam(defaultValue = "10") int limit) {
        return R.success(tagService.getHotTags(familyId, limit));
    }

    // ==================== 照片标签关联 ====================

    /**
     * 为照片添加标签
     */
    @PostMapping("/photo/{photoId}/tags")
    public R<Void> addTags(@PathVariable Long photoId,
                           @RequestBody List<Long> tagIds) {
        tagService.addTagsToPhotos(photoId, tagIds);
        return R.success();
    }

    /**
     * 移除照片标签
     */
    @DeleteMapping("/photo/{photoId}/tag/{tagId}")
    public R<Void> removeTag(@PathVariable Long photoId,
                             @PathVariable Long tagId) {
        tagService.removeTagFromPhoto(photoId, tagId);
        return R.success();
    }

    /**
     * 获取照片的所有标签
     */
    @GetMapping("/photo/{photoId}/tags")
    public R<List<TagEntity>> getPhotoTags(@PathVariable Long photoId) {
        return R.success(tagService.getPhotoTags(photoId));
    }

    /**
     * 获取标签下的所有照片ID
     */
    @GetMapping("/tag/{tagId}/photos")
    public R<List<Long>> getPhotosByTag(@PathVariable Long tagId) {
        return R.success(tagService.getPhotoIdsByTag(tagId));
    }

    // ==================== 影集照片管理 ====================

    /**
     * 添加照片到影集
     */
    @PostMapping("/album/{albumId}/photos")
    public R<Void> addPhotosToAlbum(@PathVariable Long albumId,
                                    @Valid @RequestBody AddPhotosToAlbumDTO request) {
        albumService.addPhotosToAlbum(albumId, request.getPhotoIds(), request.getAddedBy());
        return R.success();
    }

    /**
     * 从影集移除照片
     */
    @DeleteMapping("/album/{albumId}/photo/{photoId}")
    public R<Void> removePhotoFromAlbum(@PathVariable Long albumId,
                                        @PathVariable Long photoId) {
        albumService.removePhotoFromAlbum(albumId, photoId);
        return R.success();
    }

    /**
     * 获取影集中的照片ID列表（按排序）
     */
    @GetMapping("/album/{albumId}/photos")
    public R<List<Long>> getAlbumPhotos(@PathVariable Long albumId) {
        return R.success(albumService.getAlbumPhotoIds(albumId));
    }
}
