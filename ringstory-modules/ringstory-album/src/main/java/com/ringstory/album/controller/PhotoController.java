package com.ringstory.album.controller;

import com.ringstory.album.vo.PhotoDetailVO;
import com.ringstory.album.entity.AlbumEntity;
import com.ringstory.album.entity.CommentEntity;
import com.ringstory.album.entity.PhotoEntity;
import com.ringstory.album.service.AlbumService;
import com.ringstory.album.service.CommentService;
import com.ringstory.album.service.LikeService;
import com.ringstory.album.service.PhotoDeleteService;
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
    private final PhotoDeleteService photoDeleteService;

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
     * 获取时间线（支持时间范围、排序、分页）
     */
    @GetMapping("/timeline")
    @SentinelResource(value = "photoTimeline", blockHandler = "timelineBlock")
    public R<List<PhotoEntity>> timeline(@RequestParam Long familyId,
                                          @RequestParam(required = false) String startDate,
                                          @RequestParam(required = false) String endDate,
                                          @RequestParam(defaultValue = "desc") String sortOrder,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "50") int size) {
        return R.success(photoService.getTimeLine(familyId, startDate, endDate, sortOrder, page, size));
    }

    /**
     * 获取单张照片详情
     */
    @GetMapping("/photos/{photoId}")
    public R<PhotoDetailVO> getPhotoDetail(@PathVariable Long photoId,
                                            @RequestParam(required = false) Long userId) {
        return R.success(photoService.getPhotoDetail(photoId, userId));
    }

    /**
     * 点赞/取消点赞（幂等：PUT + action参数）
     */
    @PutMapping("/photos/{photoId}/like")
    @SentinelResource(value = "photoLike", blockHandler = "likeBlock")
    public R<Boolean> like(@PathVariable Long photoId,
                           @RequestParam Long userId,
                           @RequestParam(defaultValue = "like") String action) {
        boolean liked = "like".equalsIgnoreCase(action);
        return R.success(photoService.setLike(photoId, userId, liked));
    }

    /**
     * 检查是否已点赞
     */
    @GetMapping("/photos/{photoId}/like")
    public R<Boolean> checkLike(@PathVariable Long photoId, @RequestParam Long userId) {
        return R.success(likeService.isLiked(photoId, userId));
    }

    /**
     * 批量删除照片（含级联规则）
     */
    @DeleteMapping("/photos")
    public R<Map<String, Object>> batchDeletePhotos(@RequestParam List<Long> ids,
                                                     @RequestParam(required = false) Long userId) {
        Map<String, Object> result = photoDeleteService.batchDeletePhotos(ids, userId);
        return R.success(result);
    }

    /**
     * 批量为照片添加标签
     */
    @PutMapping("/photos/batch/tags")
    public R<Void> batchAddTags(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Long> photoIds = ((List<Number>) body.get("photoIds")).stream()
                .map(Number::longValue)
                .toList();
        @SuppressWarnings("unchecked")
        List<Long> tagIds = ((List<Number>) body.get("tagIds")).stream()
                .map(Number::longValue)
                .toList();
        photoService.batchAddTags(photoIds, tagIds);
        return R.success();
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
     * 删除相册（不删除照片本身）
     */
    @DeleteMapping("/album/{albumId}")
    public R<Void> deleteAlbum(@PathVariable Long albumId) {
        albumService.deleteAlbum(albumId);
        return R.success();
    }

    // ==================== Sentinel 降级方法 ====================

    public R<PhotoEntity> uploadBlock(Long familyId, Long userId, MultipartFile file, BlockException ex) {
        log.warn("照片上传被限流, familyId={}", familyId);
        return R.fail(com.ringstory.common.exception.ErrorCode.INTERNAL_ERROR, "系统繁忙，请稍后重试");
    }

    public R<List<PhotoEntity>> timelineBlock(Long familyId, String startDate, String endDate,
                                               String sortOrder, int page, int size, BlockException ex) {
        log.warn("时间线查询被限流, familyId={}", familyId);
        return R.fail(com.ringstory.common.exception.ErrorCode.INTERNAL_ERROR, "系统繁忙，请稍后重试");
    }

    public R<Boolean> likeBlock(Long photoId, Long userId, String action, BlockException ex) {
        log.warn("点赞操作被限流, photoId={}", photoId);
        return R.fail(com.ringstory.common.exception.ErrorCode.INTERNAL_ERROR, "操作过于频繁，请稍后重试");
    }
}
