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
        member.setStatus(1);
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
        InvitationEntity inv = new InvitationEntity();
        inv.setFamilyId(familyId);
        inv.setInviterId(userId);
        inv.setToken(IdUtil.fastSimpleUUID());
        inv.setExpireTime(LocalDateTime.now().plusDays(7));
        inv.setStatus("pending");
        invitationMapper.insert(inv);
        return inv;
    }
}
