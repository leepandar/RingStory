package com.ringstory.family.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ringstory.common.exception.BusinessException;
import com.ringstory.common.exception.ErrorCode;
import com.ringstory.family.entity.InvitationEntity;
import com.ringstory.family.mapper.InvitationMapper;
import com.ringstory.family.service.InvitationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 邀请服务实现类
 */
@Slf4j
@Service
public class InvitationServiceImpl extends ServiceImpl<InvitationMapper, InvitationEntity> implements InvitationService {

    @Override
    public InvitationEntity getByToken(String token) {
        return lambdaQuery()
                .eq(InvitationEntity::getToken, token)
                .one();
    }

    @Override
    public boolean isValid(InvitationEntity invitation) {
        if (invitation == null) {
            return false;
        }
        if (!"pending".equals(invitation.getStatus())) {
            return false;
        }
        if (invitation.getExpireTime().isBefore(LocalDateTime.now())) {
            return false;
        }
        if (invitation.getMaxUses() != null && invitation.getUseCount() >= invitation.getMaxUses()) {
            return false;
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void useInvitation(Long invitationId) {
        InvitationEntity invitation = getById(invitationId);
        if (!isValid(invitation)) {
            throw new BusinessException("邀请已失效");
        }
        int newUseCount = (invitation.getUseCount() == null ? 0 : invitation.getUseCount()) + 1;
        lambdaUpdate()
                .eq(InvitationEntity::getId, invitationId)
                .set(InvitationEntity::getUseCount, newUseCount)
                .update();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void expireInvitation(Long invitationId) {
        lambdaUpdate()
                .eq(InvitationEntity::getId, invitationId)
                .set(InvitationEntity::getStatus, "expired")
                .update();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InvitationEntity resetInvitation(Long invitationId) {
        InvitationEntity invitation = getById(invitationId);
        if (invitation == null) {
            throw new BusinessException(ErrorCode.INVITATION_NOT_FOUND);
        }
        int validityDays = invitation.getValidityDays() != null ? invitation.getValidityDays() : 7;
        lambdaUpdate()
                .eq(InvitationEntity::getId, invitationId)
                .set(InvitationEntity::getStatus, "pending")
                .set(InvitationEntity::getExpireTime, LocalDateTime.now().plusDays(validityDays))
                .set(InvitationEntity::getUseCount, 0)
                .update();
        log.info("邀请已重置: invitationId={}, newExpireTime={}", invitationId, LocalDateTime.now().plusDays(validityDays));
        return getById(invitationId);
    }
}
