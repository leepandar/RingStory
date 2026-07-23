package com.ringstory.family.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ringstory.family.entity.FamilyMemberEntity;
import com.ringstory.family.mapper.FamilyMemberMapper;
import com.ringstory.family.mq.NotificationEventProducer;
import com.ringstory.family.service.FamilyMemberService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class FamilyMemberServiceImpl extends ServiceImpl<FamilyMemberMapper, FamilyMemberEntity> implements FamilyMemberService {

    private final NotificationEventProducer notificationEventProducer;

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
        // 获取现有成员ID列表（在添加新成员之前）
        List<Long> existingMemberIds = listByFamilyId(familyId).stream()
                .map(FamilyMemberEntity::getUserId)
                .toList();

        FamilyMemberEntity member = new FamilyMemberEntity();
        member.setFamilyId(familyId);
        member.setUserId(userId);
        member.setRole("member");
        member.setJoinedVia(joinedVia);
        member.setIsFaceRecognized(0);
        member.setStatus(1);
        member.setJoinTime(LocalDateTime.now());
        save(member);

        // 触发新成员加入通知（异步）
        try {
            notificationEventProducer.sendNewMemberEvent(familyId, userId, existingMemberIds);
        } catch (Exception e) {
            log.warn("发送新成员通知失败: familyId={}, userId={}", familyId, userId, e);
        }

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
