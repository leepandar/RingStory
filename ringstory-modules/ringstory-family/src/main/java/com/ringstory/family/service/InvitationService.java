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

    /**
     * 重置过期/撤销的邀请链接（重新生成过期时间，状态恢复为pending）
     */
    InvitationEntity resetInvitation(Long invitationId);

    /**
     * 生成邀请链接二维码（小程序码）
     * 调用微信官方 API 生成带参数的小程序码，返回图片的 OSS URL
     *
     * @param invitation 邀请实体
     * @return 小程序码图片 URL
     */
    String generateQrCode(InvitationEntity invitation);
}
