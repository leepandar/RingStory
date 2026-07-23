package com.ringstory.album.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ringstory.album.entity.AlbumEntity;

import java.util.List;
import java.util.Set;

/**
 * 相册服务接口
 */
public interface AlbumService extends IService<AlbumEntity> {

    /**
     * 创建相册
     */
    AlbumEntity createAlbum(Long familyId, String name, Long creatorId);

    /**
     * 获取家庭下的所有相册
     */
    List<AlbumEntity> listByFamilyId(Long familyId);

    /**
     * 更新相册封面
     */
    void updateCover(Long albumId, Long coverPhotoId);

    /**
     * 增加相册照片数量
     */
    void incrementPhotoCount(Long albumId);

    /**
     * 删除相册
     */
    void deleteAlbum(Long albumId);

    /**
     * 添加照片到影集
     */
    void addPhotosToAlbum(Long albumId, List<Long> photoIds, Long addedBy);

    /**
     * 从影集移除照片
     */
    void removePhotoFromAlbum(Long albumId, Long photoId);

    /**
     * 获取影集中的照片ID列表（按排序）
     */
    List<Long> getAlbumPhotoIds(Long albumId);
}
