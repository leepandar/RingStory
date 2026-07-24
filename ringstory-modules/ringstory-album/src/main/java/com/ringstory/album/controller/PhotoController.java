package com.ringstory.album.controller;

import com.ringstory.album.vo.PhotoDetailVO;
import com.ringstory.album.dto.BatchAddTagsDTO;
import com.ringstory.album.dto.CommentDTO;
import com.ringstory.album.dto.CreateAlbumDTO;
import com.ringstory.album.entity.AlbumEntity;
import com.ringstory.album.entity.CommentEntity;
import com.ringstory.album.entity.PhotoEntity;
import com.ringstory.album.service.AlbumService;
import com.ringstory.album.service.CommentService;
import com.ringstory.album.service.LikeService;
import com.ringstory.album.service.PhotoDeleteService;
import com.ringstory.album.service.PhotoService;
import com.ringstory.album.service.StorageCalculatorService;
import com.ringstory.album.dto.StorageBreakdownDTO;
import com.ringstory.common.response.R;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import jakarta.validation.Valid;
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
    private final StorageCalculatorService storageCalculatorService;

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
    public R<Void> batchAddTags(@Valid @RequestBody BatchAddTagsDTO request) {
        photoService.batchAddTags(request.getPhotoIds(), request.getTagIds());
        return R.success();
    }

    // ==================== 评论相关 ====================

    /**
     * 添加评论
     */
    @PostMapping("/{photoId}/comment")
    public R<CommentEntity> comment(@PathVariable Long photoId,
                                    @Valid @RequestBody CommentDTO request) {
        return R.success(photoService.addComment(photoId, request.getAuthorId(),
                request.getContent(), request.getParentId()));
    }

    /**
     * 获取照片评论列表（分页）
     */
    @GetMapping("/{photoId}/comments")
    public R<List<CommentEntity>> getComments(@PathVariable Long photoId,
                                               @RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "20") int size) {
        return R.success(commentService.listByPhotoIdPaged(photoId, page, size));
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
    public R<AlbumEntity> createAlbum(@Valid @RequestBody CreateAlbumDTO request) {
        return R.success(albumService.createAlbum(request.getFamilyId(), request.getName(), request.getCreatorId()));
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

    // ==================== 存储用量 ====================

    /**
     * 查询家庭存储用量明细
     * RESTful: GET /api/album/storage/breakdown/{familyId}
     *
     * @param familyId 家庭ID
     * @return 存储明细（原片/压缩版/缩略图/视频/回收站分类统计）
     */
    @GetMapping("/storage/breakdown/{familyId}")
    public R<StorageBreakdownDTO> getStorageBreakdown(@PathVariable Long familyId) {
        StorageBreakdownDTO breakdown = storageCalculatorService.getStorageBreakdown(familyId);
        return R.success(breakdown);
    }

    /**
     * 上传前预估存储占用
     * RESTful: POST /api/album/storage/estimate
     *
     * @param fileSize 原始文件大小(bytes)
     * @param format   文件格式
     * @param width    宽度(px)
     * @param height   高度(px)
     * @return 预估总占用(bytes)
     */
    @PostMapping("/storage/estimate")
    public R<Map<String, Object>> estimateStorage(
            @RequestParam long fileSize,
            @RequestParam(defaultValue = "jpg") String format,
            @RequestParam(defaultValue = "0") int width,
            @RequestParam(defaultValue = "0") int height) {
        long estimated = storageCalculatorService.estimateStorageCost(fileSize, format, width, height);
        return R.success(Map.of(
                "originalSize", fileSize,
                "estimatedTotal", estimated,
                "format", format
        ));
    }
}
