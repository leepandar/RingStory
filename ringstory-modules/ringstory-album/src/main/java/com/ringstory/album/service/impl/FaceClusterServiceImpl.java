package com.ringstory.album.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ringstory.album.entity.FaceClusterEntity;
import com.ringstory.album.entity.FacePhotoEntity;
import com.ringstory.album.mapper.FaceClusterMapper;
import com.ringstory.album.mapper.FacePhotoMapper;
import com.ringstory.album.service.FaceClusterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 人脸聚类服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FaceClusterServiceImpl extends ServiceImpl<FaceClusterMapper, FaceClusterEntity>
        implements FaceClusterService {

    private final FacePhotoMapper facePhotoMapper;

    @Override
    public List<FaceClusterEntity> listByFamilyId(Long familyId) {
        return lambdaQuery()
                .eq(FaceClusterEntity::getFamilyId, familyId)
                .orderByDesc(FaceClusterEntity::getPhotoCount)
                .list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void renameCluster(Long clusterId, String name) {
        lambdaUpdate()
                .eq(FaceClusterEntity::getId, clusterId)
                .set(FaceClusterEntity::getName, name)
                .set(FaceClusterEntity::getStatus, 1)
                .update();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void mergeClusters(Long sourceId, Long targetId) {
        // 将 source 的所有照片关联移到 target
        facePhotoMapper.update(null,
                new LambdaQueryWrapper<FacePhotoEntity>()
                        .eq(FacePhotoEntity::getFaceClusterId, sourceId)
                        .set(FacePhotoEntity::getFaceClusterId, targetId));

        // 更新 target 的照片数量
        Long count = facePhotoMapper.selectCount(
                new LambdaQueryWrapper<FacePhotoEntity>()
                        .eq(FacePhotoEntity::getFaceClusterId, targetId)
                        .eq(FacePhotoEntity::getIsExcluded, 0));

        lambdaUpdate()
                .eq(FaceClusterEntity::getId, targetId)
                .set(FaceClusterEntity::getPhotoCount, count)
                .update();

        // 删除 source 聚类
        removeById(sourceId);
        log.info("人脸聚类合并: source={} → target={}", sourceId, targetId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void excludePhoto(Long facePhotoId) {
        facePhotoMapper.update(null,
                new LambdaQueryWrapper<FacePhotoEntity>()
                        .eq(FacePhotoEntity::getId, facePhotoId)
                        .set(FacePhotoEntity::getIsExcluded, 1));
    }

    @Override
    public List<FacePhotoEntity> getClusterPhotos(Long clusterId) {
        return facePhotoMapper.selectList(
                new LambdaQueryWrapper<FacePhotoEntity>()
                        .eq(FacePhotoEntity::getFaceClusterId, clusterId)
                        .eq(FacePhotoEntity::getIsExcluded, 0)
                        .orderByDesc(FacePhotoEntity::getCreateTime));
    }

    @Override
    public List<Long> getPersonTimeline(Long clusterId) {
        // 返回聚类下所有照片ID（按拍摄时间排序需要在关联查询中实现）
        List<FacePhotoEntity> photos = facePhotoMapper.selectList(
                new LambdaQueryWrapper<FacePhotoEntity>()
                        .eq(FacePhotoEntity::getFaceClusterId, clusterId)
                        .eq(FacePhotoEntity::getIsExcluded, 0)
                        .orderByDesc(FacePhotoEntity::getCreateTime));
        return photos.stream()
                .map(FacePhotoEntity::getPhotoId)
                .collect(Collectors.toList());
    }
}
