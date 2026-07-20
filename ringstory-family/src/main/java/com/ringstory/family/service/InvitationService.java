package com.ringstory.family.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ringstory.common.exception.BusinessException;
import com.ringstory.family.entity.InvitationEntity;
import com.ringstory.family.mapper.InvitationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 邀请服务
 */
@Slf4j
@Service
public class InvitationService extends ServiceImpl<InvitationMapper, InvitationEntity> {

    /**
     * 根据令牌获取邀请
     */
    public InvitationEntity getByToken(String token) {
        return lambdaQuery()
                .eq(InvitationEntity::getToken, token)
                .one();
    }

    /**
     * 验证邀请是否有效
     */
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

    /**
     * 使用邀请（加入家庭后调用）
     */
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

    /**
     * 过期邀请
     */
    @Transactional(rollbackFor = Exception.class)
    public void expireInvitation(Long invitationId) {
        lambdaUpdate()
                .eq(InvitationEntity::getId, invitationId)
                .set(InvitationEntity::getStatus, "expired")
                .update();
    }
}
