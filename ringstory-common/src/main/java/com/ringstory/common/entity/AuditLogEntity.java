package com.ringstory.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审计日志实体
 */
@Data
@TableName("t_audit_log")
public class AuditLogEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 操作者ID */
    private Long actorId;

    /** 家庭ID */
    private Long familyId;

    /** 操作动作 */
    private String action;

    /** 目标表名 */
    private String targetTable;

    /** 目标记录ID */
    private String targetId;

    /** 旧值 JSON */
    private String oldValue;

    /** 新值 JSON */
    private String newValue;

    /** IP 地址 */
    private String ipAddress;

    /** User-Agent */
    private String userAgent;

    /** 创建时间 */
    private LocalDateTime createTime;
}
