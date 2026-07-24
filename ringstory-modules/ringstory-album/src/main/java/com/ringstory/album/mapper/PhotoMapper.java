package com.ringstory.album.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ringstory.album.entity.PhotoEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 照片 Mapper
 */
@Mapper
public interface PhotoMapper extends BaseMapper<PhotoEntity> {

    /**
     * 按家庭ID查询照片列表（按拍摄时间倒序）
     */
    List<PhotoEntity> selectByFamilyIdOrderByShootTime(Long familyId);
}
