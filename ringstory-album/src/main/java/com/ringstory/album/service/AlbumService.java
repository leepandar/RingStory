package com.ringstory.album.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ringstory.album.entity.AlbumEntity;
import com.ringstory.album.mapper.AlbumMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 相册服务
 */
@Slf4j
@Service
public class AlbumService extends ServiceImpl<AlbumMapper, AlbumEntity> {

    /**
     * 创建相册
     */
    @Transactional(rollbackFor = Exception.class)
    public AlbumEntity createAlbum(Long familyId, String name, Long creatorId) {
        AlbumEntity album = new AlbumEntity();
        album.setFamilyId(familyId);
        album.setName(name);
        album.setCreatorId(creatorId);
        album.setAllowMemberUpload(1);
        album.setPhotoCount(0);
        save(album);
        return album;
    }

    /**
     * 获取家庭下的所有相册
     */
    public List<AlbumEntity> listByFamilyId(Long familyId) {
        return lambdaQuery()
                .eq(AlbumEntity::getFamilyId, familyId)
                .orderByDesc(AlbumEntity::getCreateTime)
                .list();
    }

    /**
     * 更新相册封面
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateCover(Long albumId, Long coverPhotoId) {
        lambdaUpdate()
                .eq(AlbumEntity::getId, albumId)
                .set(AlbumEntity::getCoverPhotoId, coverPhotoId)
                .update();
    }

    /**
     * 增加相册照片数量
     */
    @Transactional(rollbackFor = Exception.class)
    public void incrementPhotoCount(Long albumId) {
        lambdaUpdate()
                .eq(AlbumEntity::getId, albumId)
                .setSql("photo_count = photo_count + 1")
                .update();
    }

    /**
     * 删除相册
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteAlbum(Long albumId) {
        removeById(albumId);
    }
}
