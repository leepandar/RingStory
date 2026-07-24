package com.ringstory.album.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ringstory.album.entity.PhotoAlbumEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 照片影集关联 Mapper
 */
@Mapper
public interface PhotoAlbumMapper extends BaseMapper<PhotoAlbumEntity> {
}
