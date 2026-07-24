package com.ringstory.album.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ringstory.album.entity.PhotoTagEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 照片标签关联 Mapper
 */
@Mapper
public interface PhotoTagMapper extends BaseMapper<PhotoTagEntity> {
}
