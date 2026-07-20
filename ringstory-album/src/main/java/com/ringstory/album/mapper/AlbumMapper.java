package com.ringstory.album.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ringstory.album.entity.AlbumEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 相册 Mapper
 */
@Mapper
public interface AlbumMapper extends BaseMapper<AlbumEntity> {
}
