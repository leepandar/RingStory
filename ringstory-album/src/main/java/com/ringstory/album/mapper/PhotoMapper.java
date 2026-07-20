package com.ringstory.album.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ringstory.album.entity.PhotoEntity;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
@Mapper
public interface PhotoMapper extends BaseMapper<PhotoEntity> {
    List<PhotoEntity> selectByFamilyIdOrderByShootTime(Long familyId);
}
