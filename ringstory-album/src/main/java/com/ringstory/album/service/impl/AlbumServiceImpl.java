package com.ringstory.album.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ringstory.album.entity.AlbumEntity;
import com.ringstory.album.entity.PhotoAlbumEntity;
import com.ringstory.album.mapper.AlbumMapper;
import com.ringstory.album.mapper.PhotoAlbumMapper;
import com.ringstory.album.service.AlbumService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 相册服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumServiceImpl extends ServiceImpl<AlbumMapper, AlbumEntity> implements AlbumService {

    private final PhotoAlbumMapper photoAlbumMapper;

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
        // 级联删除影集照片关联
        photoAlbumMapper.delete(new LambdaQueryWrapper<PhotoAlbumEntity>()
                .eq(PhotoAlbumEntity::getAlbumId, albumId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPhotosToAlbum(Long albumId, List<Long> photoIds, Long addedBy) {
        for (Long photoId : photoIds) {
            // 检查是否已存在
            Long count = photoAlbumMapper.selectCount(new LambdaQueryWrapper<PhotoAlbumEntity>()
                    .eq(PhotoAlbumEntity::getAlbumId, albumId)
                    .eq(PhotoAlbumEntity::getPhotoId, photoId));
            if (count == 0) {
                PhotoAlbumEntity pa = new PhotoAlbumEntity();
                pa.setAlbumId(albumId);
                pa.setPhotoId(photoId);
                pa.setAddedBy(addedBy);
                pa.setSortOrder(0);
                photoAlbumMapper.insert(pa);
            }
        }
        // 更新影集照片数量
        lambdaUpdate()
                .eq(AlbumEntity::getId, albumId)
                .setSql("photo_count = (SELECT COUNT(*) FROM t_photo_album WHERE album_id = " + albumId + " AND deleted_at IS NULL)")
                .update();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removePhotoFromAlbum(Long albumId, Long photoId) {
        photoAlbumMapper.delete(new LambdaQueryWrapper<PhotoAlbumEntity>()
                .eq(PhotoAlbumEntity::getAlbumId, albumId)
                .eq(PhotoAlbumEntity::getPhotoId, photoId));
        // 更新影集照片数量
        lambdaUpdate()
                .eq(AlbumEntity::getId, albumId)
                .setSql("photo_count = (SELECT COUNT(*) FROM t_photo_album WHERE album_id = " + albumId + " AND deleted_at IS NULL)")
                .update();
    }

    @Override
    public List<Long> getAlbumPhotoIds(Long albumId) {
        List<PhotoAlbumEntity> list = photoAlbumMapper.selectList(
                new LambdaQueryWrapper<PhotoAlbumEntity>()
                        .eq(PhotoAlbumEntity::getAlbumId, albumId)
                        .orderByAsc(PhotoAlbumEntity::getSortOrder)
                        .orderByDesc(PhotoAlbumEntity::getCreateTime));
        return list.stream()
                .map(PhotoAlbumEntity::getPhotoId)
                .collect(Collectors.toList());
    }
}
