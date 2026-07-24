package com.ringstory.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通知触发服务
 * 监听业务事件，自动创建通知
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationTriggerService {

    private final NotificationService notificationService;

    /**
     * 触发点赞通知
     * @param photoOwnerId 照片所有者ID
     * @param likerId 点赞者ID
     * @param photoId 照片ID
     * @param familyId 家庭ID
     */
    public void triggerPhotoLikeNotification(Long photoOwnerId, Long likerId, Long photoId, Long familyId) {
        if (photoOwnerId.equals(likerId)) {
            // 自己点赞自己不通知
            return;
        }
        log.info("触发点赞通知: photoOwnerId={}, likerId={}, photoId={}", photoOwnerId, likerId, photoId);
        notificationService.sendNotificationWithPreference(
                photoOwnerId, familyId, "photo_like",
                "有人赞了你的照片",
                "一位家庭成员赞了你的照片",
                "/pages/photo/detail?id=" + photoId
        );
    }

    /**
     * 触发评论通知
     * @param photoOwnerId 照片所有者ID
     * @param commenterId 评论者ID
     * @param photoId 照片ID
     * @param familyId 家庭ID
     * @param replyToUserId 被回复者ID（可为null）
     */
    public void triggerCommentNotification(Long photoOwnerId, Long commenterId, Long photoId,
                                            Long familyId, Long replyToUserId) {
        if (photoOwnerId.equals(commenterId) && replyToUserId == null) {
            return;
        }
        log.info("触发评论通知: photoOwnerId={}, commenterId={}, photoId={}", photoOwnerId, commenterId, photoId);
        // 通知照片上传者
        if (!photoOwnerId.equals(commenterId)) {
            notificationService.sendNotificationWithPreference(
                    photoOwnerId, familyId, "new_comment",
                    "有人评论了你的照片",
                    "一位家庭成员评论了你的照片",
                    "/pages/photo/detail?id=" + photoId
            );
        }
        // 通知被回复者
        if (replyToUserId != null && !replyToUserId.equals(commenterId)) {
            notificationService.sendNotificationWithPreference(
                    replyToUserId, familyId, "new_comment",
                    "有人回复了你的评论",
                    "一位家庭成员回复了你的评论",
                    "/pages/photo/detail?id=" + photoId
            );
        }
    }

    /**
     * 触发新成员加入通知
     * @param familyId 家庭ID
     * @param newMemberId 新成员ID
     * @param existingMemberIds 现有成员ID列表
     */
    public void triggerNewMemberNotification(Long familyId, Long newMemberId, List<Long> existingMemberIds) {
        log.info("触发新成员通知: familyId={}, newMemberId={}", familyId, newMemberId);
        for (Long memberId : existingMemberIds) {
            if (!memberId.equals(newMemberId)) {
                notificationService.sendNotificationWithPreference(
                        memberId, familyId, "new_member",
                        "新成员加入",
                        "一位新成员加入了你的家庭",
                        "/pages/family/members"
                );
            }
        }
    }

    /**
     * 触发回顾完成通知
     * @param familyId 家庭ID
     * @param memberIds 家庭成员ID列表
     * @param reviewTitle 回顾标题
     */
    public void triggerReviewCompleteNotification(Long familyId, List<Long> memberIds, String reviewTitle) {
        log.info("触发回顾完成通知: familyId={}, reviewTitle={}", familyId, reviewTitle);
        for (Long memberId : memberIds) {
            notificationService.sendNotificationWithPreference(
                    memberId, familyId, "review_complete",
                    "新年轮回顾已生成",
                    reviewTitle + " 已生成，快去看看吧",
                    "/pages/review/index"
            );
        }
    }
}
