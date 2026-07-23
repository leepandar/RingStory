package com.ringstory.family.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ringstory.family.entity.InvitationEntity;
import com.ringstory.family.service.InvitationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 邀请过期定时任务
 * 自动将已过期的邀请状态更新为 expired
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InvitationExpireTask {

    private final InvitationService invitationService;

    /**
     * 每小时执行一次，检查并过期超时的邀请
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void expireInvitations() {
        log.info("开始检查过期邀请...");
        try {
            List<InvitationEntity> expiredInvitations = invitationService.list(
                    new LambdaQueryWrapper<InvitationEntity>()
                            .eq(InvitationEntity::getStatus, "pending")
                            .lt(InvitationEntity::getExpireTime, LocalDateTime.now()));

            int count = 0;
            for (InvitationEntity invitation : expiredInvitations) {
                invitationService.expireInvitation(invitation.getId());
                count++;
            }

            log.info("过期邀请处理完成: 共处理 {} 条", count);
        } catch (Exception e) {
            log.error("过期邀请处理失败", e);
        }
    }
}
