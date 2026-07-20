package com.ringstory.album.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ringstory.album.entity.LikeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
@Mapper
public interface LikeMapper extends BaseMapper<LikeEntity> {
    int deleteByPhotoIdAndUserId(@Param("photoId") Long photoId, @Param("userId") Long userId);
}
