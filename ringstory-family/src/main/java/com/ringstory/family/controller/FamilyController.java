package com.ringstory.family.controller;

import com.ringstory.common.response.R;
import com.ringstory.family.entity.FamilyEntity;
import com.ringstory.family.entity.FamilyMemberEntity;
import com.ringstory.family.entity.InvitationEntity;
import com.ringstory.family.service.FamilyMemberService;
import com.ringstory.family.service.FamilyService;
import com.ringstory.family.service.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 家庭控制器
 */
@RestController
@RequestMapping("/api/family")
@RequiredArgsConstructor
public class FamilyController {

    private final FamilyService familyService;
    private final FamilyMemberService familyMemberService;
    private final InvitationService invitationService;

    // ==================== 家庭相关 ====================

    /**
     * 创建家庭
     */
    @PostMapping
    public R<FamilyEntity> create(@RequestBody Map<String, String> body) {
        Long userId = Long.valueOf(body.get("userId"));
        return R.success(familyService.createFamily(body.get("name"), userId));
    }

    /**
     * 获取家庭详情
     */
    @GetMapping("/{id}")
    public R<FamilyEntity> get(@PathVariable Long id) {
        return R.success(familyService.getById(id));
    }

    // ==================== 成员相关 ====================

    /**
     * 获取家庭成员列表
     */
    @GetMapping("/{id}/members")
    public R<List<FamilyMemberEntity>> getMembers(@PathVariable Long id) {
        return R.success(familyMemberService.listByFamilyId(id));
    }

    /**
     * 通过邀请加入家庭
     */
    @PostMapping("/join")
    public R<FamilyMemberEntity> join(@RequestParam String token,
                                      @RequestParam Long userId) {
        InvitationEntity invitation = invitationService.getByToken(token);
        if (!invitationService.isValid(invitation)) {
            return R.notFound("邀请不存在或已失效");
        }
        FamilyMemberEntity member = familyMemberService.addMember(
                invitation.getFamilyId(), userId, invitation.getId());
        invitationService.useInvitation(invitation.getId());
        return R.success(member);
    }

    /**
     * 更新成员角色
     */
    @PutMapping("/member/{memberId}/role")
    public R<Void> updateRole(@PathVariable Long memberId,
                              @RequestParam String role) {
        familyMemberService.updateRole(memberId, role);
        return R.success();
    }

    /**
     * 移除成员
     */
    @DeleteMapping("/member/{memberId}")
    public R<Void> removeMember(@PathVariable Long memberId) {
        familyMemberService.removeMember(memberId);
        return R.success();
    }

    // ==================== 邀请相关 ====================

    /**
     * 创建邀请
     */
    @PostMapping("/{id}/invitation")
    public R<InvitationEntity> createInvitation(@PathVariable Long id,
                                                 @RequestParam Long userId) {
        return R.success(familyService.createInvitation(id, userId));
    }

    /**
     * 查询邀请详情
     */
    @GetMapping("/invitation/{id}")
    public R<InvitationEntity> getInvitation(@PathVariable Long id) {
        return R.success(invitationService.getById(id));
    }

    // ==================== 存储管理 ====================

    /**
     * 更新家庭存储用量（供 album-svc 调用）
     */
    @PutMapping("/{id}/storage")
    public R<Void> updateStorage(@PathVariable Long id,
                                 @RequestParam long bytes) {
        familyService.incrementStorageUsed(id, bytes);
        return R.success();
    }
}
