package com.ringstory.family.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ringstory.family.entity.FamilyMemberEntity;
import com.ringstory.family.mapper.FamilyMemberMapper;
import com.ringstory.family.service.FamilyMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 家庭成员服务实现类
 */
@Slf4j
@Service
public class FamilyMemberServiceImpl extends ServiceImpl<FamilyMemberMapper, FamilyMemberEntity> implements FamilyMemberService {

    @Override
    public List<FamilyMemberEntity> listByFamilyId(Long familyId) {
        return baseMapper.findByFamilyId(familyId);
    }

    @Override
    public FamilyMemberEntity getByUserAndFamily(Long userId, Long familyId) {
        return lambdaQuery()
                .eq(FamilyMemberEntity::getUserId, userId)
                .eq(FamilyMemberEntity::getFamilyId, familyId)
                .one();
    }

    @Override
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRole(Long memberId, String role) {
        lambdaUpdate()
                .eq(FamilyMemberEntity::getId, memberId)
                .set(FamilyMemberEntity::getRole, role)
                .update();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeMember(Long memberId) {
        removeById(memberId);
    }
}
