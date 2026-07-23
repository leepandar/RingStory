package com.ringstory.album.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ringstory.album.entity.AlbumEntity;
import com.ringstory.album.mapper.AlbumMapper;
import com.ringstory.album.service.AlbumService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 相册服务实现类
 */
@Slf4j
@Service
public class AlbumServiceImpl extends ServiceImpl<AlbumMapper, AlbumEntity> implements AlbumService {

    @Override
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

    @Override
    public List<AlbumEntity> listByFamilyId(Long familyId) {
        return lambdaQuery()
                .eq(AlbumEntity::getFamilyId, familyId)
                .orderByDesc(AlbumEntity::getCreateTime)
                .list();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCover(Long albumId, Long coverPhotoId) {
        lambdaUpdate()
                .eq(AlbumEntity::getId, albumId)
                .set(AlbumEntity::getCoverPhotoId, coverPhotoId)
                .update();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementPhotoCount(Long albumId) {
        lambdaUpdate()
                .eq(AlbumEntity::getId, albumId)
                .setSql("photo_count = photo_count + 1")
                .update();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAlbum(Long albumId) {
        removeById(albumId);
    }
}
