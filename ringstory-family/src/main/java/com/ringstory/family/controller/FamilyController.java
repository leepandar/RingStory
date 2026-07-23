package com.ringstory.family.controller;

import com.ringstory.common.exception.BusinessException;
import com.ringstory.common.exception.ErrorCode;
import com.ringstory.common.response.R;
import com.ringstory.family.dto.MemberRoleUpdateDTO;
import com.ringstory.family.entity.FamilyEntity;
import com.ringstory.family.entity.FamilyMemberEntity;
import com.ringstory.family.entity.InvitationEntity;
import com.ringstory.family.service.FamilyMemberService;
import com.ringstory.family.service.FamilyService;
import com.ringstory.family.service.InvitationService;
import jakarta.validation.Valid;
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
        FamilyEntity family = familyService.getById(id);
        if (family == null) {
            throw new BusinessException(ErrorCode.FAMILY_NOT_FOUND);
        }
        return R.success(family);
    }

    // ==================== 成员相关 ====================

    /**
     * 获取家庭成员列表（分页）
     */
    @GetMapping("/{id}/members")
    public R<List<FamilyMemberEntity>> getMembers(@PathVariable Long id,
                                                   @RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "20") int size) {
        return R.success(familyMemberService.listByFamilyIdPaged(id, page, size));
    }

    /**
     * 通过邀请加入家庭（幂等：PUT）
     */
    @PutMapping("/{familyId}/members/{userId}")
    public R<FamilyMemberEntity> join(@PathVariable Long familyId,
                                       @PathVariable Long userId,
                                       @RequestParam String token) {
        InvitationEntity invitation = invitationService.getByToken(token);
        if (invitation == null) {
            throw new BusinessException(ErrorCode.INVITATION_NOT_FOUND);
        }
        // 详细错误码区分
        if ("expired".equals(invitation.getStatus())) {
            throw new BusinessException(ErrorCode.INVITATION_EXPIRED);
        }
        if ("revoked".equals(invitation.getStatus())) {
            throw new BusinessException(ErrorCode.INVITATION_REVOKED);
        }
        if (!invitationService.isValid(invitation)) {
            if (invitation.getMaxUses() != null && invitation.getUseCount() >= invitation.getMaxUses()) {
                throw new BusinessException(ErrorCode.INVITATION_USED);
            }
            throw new BusinessException(ErrorCode.INVITATION_EXPIRED);
        }
        FamilyMemberEntity member = familyMemberService.addMember(
                invitation.getFamilyId(), userId, invitation.getId());
        invitationService.useInvitation(invitation.getId());
        return R.success(member);
    }

    /**
     * 更新成员角色（PATCH + Body DTO）
     */
    @PatchMapping("/members/{memberId}")
    public R<Void> updateRole(@PathVariable Long memberId,
                              @Valid @RequestBody MemberRoleUpdateDTO request) {
        // 验证角色值
        if (!List.of("admin", "member", "viewer").contains(request.getRole())) {
            throw new BusinessException(ErrorCode.ROLE_INVALID);
        }
        familyMemberService.updateRole(memberId, request.getRole());
        return R.success();
    }

    /**
     * 移除成员（支持是否删除成员照片参数）
     */
    @DeleteMapping("/members/{memberId}")
    public R<Void> removeMember(@PathVariable Long memberId,
                                 @RequestParam(defaultValue = "false") boolean deletePhotos) {
        familyMemberService.removeMember(memberId, deletePhotos);
        return R.success();
    }

    // ==================== 邀请相关 ====================

    /**
     * 创建邀请（支持自定义有效天数）
     */
    @PostMapping("/{id}/invitation")
    public R<InvitationEntity> createInvitation(@PathVariable Long id,
                                                 @RequestParam Long userId,
                                                 @RequestParam(defaultValue = "7") int validityDays) {
        // 校验有效期范围
        if (validityDays < 1 || validityDays > 30) {
            throw new BusinessException(ErrorCode.INVALID_PARAMS, "有效天数需在1-30之间");
        }
        return R.success(familyService.createInvitation(id, userId, validityDays));
    }

    /**
     * 查询邀请详情
     */
    @GetMapping("/invitation/{id}")
    public R<InvitationEntity> getInvitation(@PathVariable Long id) {
        return R.success(invitationService.getById(id));
    }

    /**
     * 重置过期的邀请链接
     */
    @PostMapping("/invitations/{invitationId}/reset")
    public R<InvitationEntity> resetInvitation(@PathVariable Long invitationId) {
        return R.success(invitationService.resetInvitation(invitationId));
    }

    // ==================== 人脸聚类设置 ====================

    /**
     * 设置家庭人脸聚类开关
     */
    @PutMapping("/{familyId}/face-cluster-setting")
    public R<Void> setFaceClusterSetting(@PathVariable Long familyId,
                                          @RequestParam boolean enabled) {
        familyService.setFaceClusterEnabled(familyId, enabled);
        return R.success();
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
