package com.ringstory.family.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ringstory.family.entity.FamilyMemberEntity;
import com.ringstory.family.mapper.FamilyMemberMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 家庭成员服务
 */
@Slf4j
@Service
public class FamilyMemberService extends ServiceImpl<FamilyMemberMapper, FamilyMemberEntity> {

    /**
     * 获取家庭下的所有成员
     */
    public List<FamilyMemberEntity> listByFamilyId(Long familyId) {
        return baseMapper.findByFamilyId(familyId);
    }

    /**
     * 根据用户ID和家庭ID获取成员
     */
    public FamilyMemberEntity getByUserAndFamily(Long userId, Long familyId) {
        return lambdaQuery()
                .eq(FamilyMemberEntity::getUserId, userId)
                .eq(FamilyMemberEntity::getFamilyId, familyId)
                .one();
    }

    /**
     * 添加成员到家庭
     */
    @Transactional(rollbackFor = Exception.class)
    public FamilyMemberEntity addMember(Long familyId, Long userId, Long joinedVia) {
        FamilyMemberEntity member = new FamilyMemberEntity();
        member.setFamilyId(familyId);
        member.setUserId(userId);
        member.setRole("member");
        member.setJoinedVia(joinedVia);
        member.setIsFaceRecognized(0);
        member.setStatus(1);
        member.setJoinTime(LocalDateTime.now());
        save(member);
        return member;
    }

    /**
     * 更新成员角色
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(Long memberId, String role) {
        lambdaUpdate()
                .eq(FamilyMemberEntity::getId, memberId)
                .set(FamilyMemberEntity::getRole, role)
                .update();
    }

    /**
     * 移除成员（逻辑删除）
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeMember(Long memberId) {
        removeById(memberId);
    }
}
