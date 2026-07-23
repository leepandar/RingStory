package com.ringstory.album.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ringstory.album.entity.FaceClusterEntity;
import com.ringstory.album.entity.FacePhotoEntity;

import java.util.List;

/**
 * 人脸聚类服务接口
 */
public interface FaceClusterService extends IService<FaceClusterEntity> {

    /**
     * 获取家庭下的人物列表
     */
    List<FaceClusterEntity> listByFamilyId(Long familyId);

    /**
     * 重命名聚类
     */
    void renameCluster(Long clusterId, String name);

    /**
     * 合并两个聚类
     */
    void mergeClusters(Long sourceId, Long targetId);

    /**
     * 从聚类中排除照片
     */
    void excludePhoto(Long facePhotoId);

    /**
     * 获取聚类下的照片列表
     */
    List<FacePhotoEntity> getClusterPhotos(Long clusterId);

    /**
     * 获取人物时间线（按拍摄时间排序的照片列表）
     */
    List<Long> getPersonTimeline(Long clusterId);
}
