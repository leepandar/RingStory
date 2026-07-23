package com.ringstory.family.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ringstory.family.entity.InvitationEntity;

/**
 * 邀请服务接口
 */
public interface InvitationService extends IService<InvitationEntity> {

    /**
     * 根据令牌获取邀请
     */
    InvitationEntity getByToken(String token);

    /**
     * 验证邀请是否有效
     */
    boolean isValid(InvitationEntity invitation);

    /**
     * 使用邀请（加入家庭后调用）
     */
    void useInvitation(Long invitationId);

    /**
     * 过期邀请
     */
    void expireInvitation(Long invitationId);
}
