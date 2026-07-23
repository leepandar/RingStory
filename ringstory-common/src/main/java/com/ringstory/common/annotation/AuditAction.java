package com.ringstory.common.annotation;

/**
 * 审计操作类型枚举
 * P0 必须记录的操作：成员移除、权限变更、照片违规拦截、存储上限调整、邀请生成/撤销
 */
public enum AuditAction {
    /** P0: 成员移除 */
    MEMBER_REMOVE,
    /** P0: 权限变更 */
    ROLE_CHANGE,
    /** P0: 照片违规拦截 */
    PHOTO_VIOLATION,
    /** P0: 存储上限调整 */
    STORAGE_LIMIT_CHANGE,
    /** P0: 邀请生成 */
    INVITATION_CREATE,
    /** P0: 邀请撤销 */
    INVITATION_REVOKE,
    /** P0: 邀请重置 */
    INVITATION_RESET,

    /** P1: 家庭创建 */
    FAMILY_CREATE,
    /** P1: 家庭删除 */
    FAMILY_DELETE,
    /** P1: 照片批量删除 */
    PHOTO_BATCH_DELETE,
    /** P1: 人脸聚类命名 */
    FACE_CLUSTER_RENAME,
    /** P1: 人脸聚类合并 */
    FACE_CLUSTER_MERGE,
    /** P1: 回顾生成 */
    REVIEW_GENERATE,
    /** P1: 管理后台登录 */
    ADMIN_LOGIN
}
