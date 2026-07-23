package com.ringstory.family.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ringstory.family.entity.FamilyEntity;
import com.ringstory.family.entity.FamilyMemberEntity;
import com.ringstory.family.entity.InvitationEntity;

import java.util.List;

/**
 * 家庭服务接口
 */
public interface FamilyService extends IService<FamilyEntity> {

    /**
     * 创建家庭并自动将创建者加入成员
     */
    FamilyEntity createFamily(String name, Long userId);

    /**
     * 获取家庭成员列表
     */
    List<FamilyMemberEntity> getMembers(Long familyId);

    /**
     * 创建家庭邀请
     */
    InvitationEntity createInvitation(Long familyId, Long userId);

    /**
     * 增加家庭已用存储量
     */
    void incrementStorageUsed(Long familyId, long bytes);
}
