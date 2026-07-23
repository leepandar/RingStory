package com.ringstory.album.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ringstory.album.entity.AlbumEntity;

import java.util.List;

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
}
