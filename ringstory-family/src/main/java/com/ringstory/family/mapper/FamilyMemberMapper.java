package com.ringstory.family.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ringstory.family.entity.FamilyMemberEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 家庭成员 Mapper
 */
@Mapper
public interface FamilyMemberMapper extends BaseMapper<FamilyMemberEntity> {

    /**
     * 根据家庭ID查询成员列表
     */
    List<FamilyMemberEntity> findByFamilyId(Long familyId);
}
