package com.ringstory.album.controller;

import com.ringstory.album.entity.AlbumEntity;
import com.ringstory.album.entity.CommentEntity;
import com.ringstory.album.entity.PhotoEntity;
import com.ringstory.album.service.AlbumService;
import com.ringstory.album.service.CommentService;
import com.ringstory.album.service.LikeService;
import com.ringstory.album.service.PhotoService;
import com.ringstory.common.response.R;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 照片控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/album")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;
    private final AlbumService albumService;
    private final CommentService commentService;
    private final LikeService likeService;

    // ==================== 照片相关 ====================

    /**
     * 上传照片
     */
    @PostMapping("/upload")
    @SentinelResource(value = "photoUpload", blockHandler = "uploadBlock")
    public R<PhotoEntity> upload(@RequestParam Long familyId,
                                 @RequestParam Long userId,
                                 @RequestParam("file") MultipartFile file) {
        return R.success(photoService.uploadPhoto(familyId, userId, file));
    }

    /**
     * 获取时间线
     */
    @GetMapping("/timeline")
    @SentinelResource(value = "photoTimeline", blockHandler = "timelineBlock")
    public R<List<PhotoEntity>> timeline(@RequestParam Long familyId) {
        return R.success(photoService.getTimeLine(familyId));
    }

    /**
     * 点赞/取消点赞
     */
    @PostMapping("/{photoId}/like")
    @SentinelResource(value = "photoLike", blockHandler = "likeBlock")
    public R<Boolean> like(@PathVariable Long photoId, @RequestParam Long userId) {
        return R.success(photoService.toggleLike(photoId, userId));
    }

    /**
     * 检查是否已点赞
     */
    @GetMapping("/{photoId}/like/check")
    public R<Boolean> checkLike(@PathVariable Long photoId, @RequestParam Long userId) {
        return R.success(likeService.isLiked(photoId, userId));
    }

    // ==================== 评论相关 ====================

    /**
     * 添加评论
     */
    @PostMapping("/{photoId}/comment")
    public R<CommentEntity> comment(@PathVariable Long photoId,
                                    @RequestBody Map<String, Object> body) {
        Long authorId = Long.valueOf(body.get("authorId").toString());
        String content = body.get("content").toString();
        Long parentId = body.containsKey("parentId")
                ? Long.valueOf(body.get("parentId").toString()) : null;
        return R.success(photoService.addComment(photoId, authorId, content, parentId));
    }

    /**
     * 获取照片评论列表
     */
    @GetMapping("/{photoId}/comments")
    public R<List<CommentEntity>> getComments(@PathVariable Long photoId) {
        return R.success(commentService.listByPhotoId(photoId));
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/comment/{commentId}")
    public R<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return R.success();
    }

    // ==================== 相册相关 ====================

    /**
     * 创建相册
     */
    @PostMapping("/album")
    public R<AlbumEntity> createAlbum(@RequestBody Map<String, Object> body) {
        Long familyId = Long.valueOf(body.get("familyId").toString());
        String name = body.get("name").toString();
        Long creatorId = Long.valueOf(body.get("creatorId").toString());
        return R.success(albumService.createAlbum(familyId, name, creatorId));
    }

    /**
     * 获取家庭相册列表
     */
    @GetMapping("/album/list")
    public R<List<AlbumEntity>> listAlbums(@RequestParam Long familyId) {
        return R.success(albumService.listByFamilyId(familyId));
    }

    /**
     * 更新相册封面
     */
    @PutMapping("/album/{albumId}/cover")
    public R<Void> updateCover(@PathVariable Long albumId,
                               @RequestParam Long coverPhotoId) {
        albumService.updateCover(albumId, coverPhotoId);
        return R.success();
    }

    /**
     * 删除相册
     */
    @DeleteMapping("/album/{albumId}")
    public R<Void> deleteAlbum(@PathVariable Long albumId) {
        albumService.deleteAlbum(albumId);
        return R.success();
    }

    // ==================== Sentinel 降级方法 ====================

    public R<PhotoEntity> uploadBlock(Long familyId, Long userId, MultipartFile file, BlockException ex) {
        log.warn("照片上传被限流, familyId={}", familyId);
        return R.fail("系统繁忙，请稍后重试");
    }

    public R<List<PhotoEntity>> timelineBlock(Long familyId, BlockException ex) {
        log.warn("时间线查询被限流, familyId={}", familyId);
        return R.fail("系统繁忙，请稍后重试");
    }

    public R<Boolean> likeBlock(Long photoId, Long userId, BlockException ex) {
        log.warn("点赞操作被限流, photoId={}", photoId);
        return R.fail("操作过于频繁，请稍后重试");
    }
}
