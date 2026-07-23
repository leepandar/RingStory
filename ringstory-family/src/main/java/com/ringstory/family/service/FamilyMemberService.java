package com.ringstory.family.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ringstory.family.entity.FamilyMemberEntity;

import java.util.List;

/**
 * 家庭成员服务接口
 */
public interface FamilyMemberService extends IService<FamilyMemberEntity> {

    /**
     * 获取家庭下的所有成员
     */
    List<FamilyMemberEntity> listByFamilyId(Long familyId);

    /**
     * 获取家庭下的所有成员（分页）
     */
    List<FamilyMemberEntity> listByFamilyIdPaged(Long familyId, int page, int size);

    /**
     * 根据用户ID和家庭ID获取成员
     */
    FamilyMemberEntity getByUserAndFamily(Long userId, Long familyId);

    /**
     * 添加成员到家庭
     */
    FamilyMemberEntity addMember(Long familyId, Long userId, Long joinedVia);

    /**
     * 更新成员角色
     */
    void updateRole(Long memberId, String role);

    /**
     * 移除成员（逻辑删除）
     * @param deletePhotos 是否同时删除成员的照片
     */
    void removeMember(Long memberId, boolean deletePhotos);
}
