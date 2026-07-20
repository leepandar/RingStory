package com.ringstory.family.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ringstory.family.entity.FamilyMemberEntity;
import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface FamilyMemberMapper extends BaseMapper<FamilyMemberEntity> {
    java.util.List<FamilyMemberEntity> findByFamilyId(Long familyId);
}
