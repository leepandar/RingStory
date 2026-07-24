package com.ringstory.album.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ringstory.album.entity.LikeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 点赞 Mapper
 */
@Mapper
public interface LikeMapper extends BaseMapper<LikeEntity> {

    /**
     * 根据照片ID和用户ID删除点赞
     */
    int deleteByPhotoIdAndUserId(@Param("photoId") Long photoId, @Param("userId") Long userId);
}
