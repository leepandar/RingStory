package com.ringstory.album.controller;

import com.ringstory.album.entity.FaceClusterEntity;
import com.ringstory.album.entity.FacePhotoEntity;
import com.ringstory.album.service.FaceClusterService;
import com.ringstory.common.response.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 人脸聚类控制器
 */
@RestController
@RequestMapping("/api/face")
@RequiredArgsConstructor
public class FaceController {

    private final FaceClusterService faceClusterService;

    /**
     * 获取家庭人物列表
     */
    @GetMapping("/list")
    public R<List<FaceClusterEntity>> listFaces(@RequestParam Long familyId) {
        return R.success(faceClusterService.listByFamilyId(familyId));
    }

    /**
     * 重命名人物聚类
     */
    @PutMapping("/cluster/{clusterId}/name")
    public R<Void> renameCluster(@PathVariable Long clusterId,
                                 @RequestBody Map<String, String> body) {
        faceClusterService.renameCluster(clusterId, body.get("name"));
        return R.success();
    }

    /**
     * 合并两个人物聚类
     */
    @PostMapping("/cluster/{sourceId}/merge/{targetId}")
    public R<Void> mergeClusters(@PathVariable Long sourceId,
                                 @PathVariable Long targetId) {
        faceClusterService.mergeClusters(sourceId, targetId);
        return R.success();
    }

    /**
     * 获取聚类下的照片
     */
    @GetMapping("/cluster/{clusterId}/photos")
    public R<List<FacePhotoEntity>> getClusterPhotos(@PathVariable Long clusterId) {
        return R.success(faceClusterService.getClusterPhotos(clusterId));
    }

    /**
     * 获取人物时间线
     */
    @GetMapping("/cluster/{clusterId}/timeline")
    public R<List<Long>> getPersonTimeline(@PathVariable Long clusterId) {
        return R.success(faceClusterService.getPersonTimeline(clusterId));
    }

    /**
     * 排除照片（手动标记不是同一个人）
     */
    @PostMapping("/photo/{facePhotoId}/exclude")
    public R<Void> excludePhoto(@PathVariable Long facePhotoId) {
        faceClusterService.excludePhoto(facePhotoId);
        return R.success();
    }
}
