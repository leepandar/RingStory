package com.ringstory.family.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ringstory.family.entity.FamilyEntity;
import com.ringstory.family.entity.FamilyMemberEntity;
import com.ringstory.family.entity.InvitationEntity;
import com.ringstory.family.mapper.FamilyMapper;
import com.ringstory.family.mapper.FamilyMemberMapper;
import com.ringstory.family.mapper.InvitationMapper;
import com.ringstory.family.service.FamilyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 家庭服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FamilyServiceImpl extends ServiceImpl<FamilyMapper, FamilyEntity> implements FamilyService {

    private final FamilyMemberMapper memberMapper;
    private final InvitationMapper invitationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FamilyEntity createFamily(String name, Long userId) {
        FamilyEntity family = new FamilyEntity();
        family.setName(name);
        family.setCreatedBy(userId);
        family.setJoinType(0);
        family.setMemberCount(1);
        family.setStorageLimit(10737418240L);
        save(family);

        FamilyMemberEntity member = new FamilyMemberEntity();
        member.setFamilyId(family.getId());
        member.setUserId(userId);
        member.setRole("admin");
        member.setJoinTime(LocalDateTime.now());
        memberMapper.insert(member);
        return family;
    }

    @Override
    public List<FamilyMemberEntity> getMembers(Long familyId) {
        return memberMapper.findByFamilyId(familyId);
    }

    @Override
    public InvitationEntity createInvitation(Long familyId, Long userId) {
        return createInvitation(familyId, userId, 7);
    }

    @Override
    public InvitationEntity createInvitation(Long familyId, Long userId, int validityDays) {
        // 检查待使用邀请数量上限（10个）
        long pendingCount = invitationMapper.selectCount(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<InvitationEntity>()
                        .eq(InvitationEntity::getFamilyId, familyId)
                        .eq(InvitationEntity::getStatus, "pending"));
        if (pendingCount >= 10) {
            throw new com.ringstory.common.exception.BusinessException(
                    com.ringstory.common.exception.ErrorCode.INVITATION_LIMIT_EXCEEDED);
        }

        InvitationEntity inv = new InvitationEntity();
        inv.setFamilyId(familyId);
        inv.setInviterId(userId);
        inv.setToken(IdUtil.fastSimpleUUID());
        inv.setExpireTime(LocalDateTime.now().plusDays(validityDays));
        inv.setValidityDays(validityDays);
        inv.setStatus("pending");
        invitationMapper.insert(inv);
        return inv;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void incrementStorageUsed(Long familyId, long bytes) {
        lambdaUpdate()
                .eq(FamilyEntity::getId, familyId)
                .setSql("storage_used = storage_used + " + bytes)
                .update();
    }

    @Override
    public void setFaceClusterEnabled(Long familyId, boolean enabled) {
        lambdaUpdate()
                .eq(FamilyEntity::getId, familyId)
                .set(FamilyEntity::getAutoFaceCluster, enabled ? 1 : 0)
                .update();
        log.info("人脸聚类开关已更新: familyId={}, enabled={}", familyId, enabled);
    }
}
