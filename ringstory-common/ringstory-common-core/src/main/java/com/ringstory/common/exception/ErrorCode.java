package com.ringstory.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 全局错误码枚举
 * 统一前后端错误响应规范
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ==================== 通用错误码 ====================
    INVALID_PARAMS(400, "参数无效"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "资源冲突"),
    INTERNAL_ERROR(500, "系统内部错误"),

    // ==================== 邀请相关 ====================
    INVITATION_EXPIRED(1001, "邀请已过期"),
    INVITATION_USED(1002, "邀请已被使用"),
    INVITATION_REVOKED(1003, "邀请已被撤销"),
    INVITATION_NOT_FOUND(1004, "邀请不存在"),
    INVITATION_LIMIT_EXCEEDED(1005, "待使用邀请数量已达上限（最多10个）"),

    // ==================== 家庭相关 ====================
    FAMILY_MEMBER_LIMIT_EXCEEDED(2001, "家庭成员数量已达上限"),
    FAMILY_NOT_FOUND(2002, "家庭不存在"),
    FAMILY_FORBIDDEN(2003, "无权操作该家庭"),
    MEMBER_NOT_FOUND(2004, "成员不存在"),
    ROLE_INVALID(2005, "无效的角色值"),

    // ==================== 照片相关 ====================
    PHOTO_NOT_FOUND(3001, "照片不存在"),
    PHOTO_ALREADY_DELETED(3002, "照片已被删除"),
    STORAGE_LIMIT_EXCEEDED(3003, "存储空间已达上限"),
    STORAGE_WARNING(3004, "存储空间即将达到上限"),
    FILE_FORMAT_UNSUPPORTED(3005, "不支持的文件格式"),
    FILE_TOO_LARGE(3006, "文件过大"),

    // ==================== 备注相关 ====================
    NOTE_NOT_FOUND(4001, "备注不存在"),
    NOTE_CONFLICT(4002, "备注已被他人修改，请刷新后重试"),

    // ==================== 评论/点赞相关 ====================
    COMMENT_NOT_FOUND(5001, "评论不存在"),
    ALREADY_LIKED(5002, "已经点赞"),
    NOT_LIKED(5003, "尚未点赞"),

    // ==================== 搜索相关 ====================
    SEARCH_PARAM_INVALID(6001, "搜索参数无效"),

    // ==================== 回顾相关 ====================
    REVIEW_GENERATE_FAILED(7001, "回顾生成失败"),
    REVIEW_SERVICE_DEGRADED(7002, "回顾服务降级，已切换为拼图海报模式"),

    // ==================== 上传相关 ====================
    UPLOAD_TASK_NOT_FOUND(8001, "上传任务不存在或已过期"),
    UPLOAD_PART_INVALID(8002, "分片序号无效"),
    UPLOAD_INCOMPLETE(8003, "部分分片尚未上传");

    /** 错误码 */
    private final int code;

    /** 默认消息 */
    private final String message;
}
