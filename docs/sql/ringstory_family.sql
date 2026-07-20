-- ============================================================
-- RingStory 家庭数据库初始化脚本
-- Database: ringstory_family
-- ============================================================

CREATE DATABASE IF NOT EXISTS `ringstory_family` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `ringstory_family`;

-- -----------------------------------------------------------
-- 家庭表
-- -----------------------------------------------------------
CREATE TABLE `t_family` (
    `id`                  BIGINT        NOT NULL                COMMENT '主键ID（雪花算法）',
    `name`                VARCHAR(64)   NOT NULL                COMMENT '家庭名称',
    `cover_url`           VARCHAR(512)  DEFAULT NULL            COMMENT '封面URL',
    `created_by`          BIGINT        NOT NULL                COMMENT '创建者ID',
    `join_type`           TINYINT       DEFAULT 0               COMMENT '加入方式（0-邀请 1-链接）',
    `member_count`        INT           DEFAULT 0               COMMENT '成员数',
    `photo_count`         INT           DEFAULT 0               COMMENT '照片数',
    `storage_used`        BIGINT        DEFAULT 0               COMMENT '已用存储(bytes)',
    `storage_limit`       BIGINT        DEFAULT 10737418240     COMMENT '存储上限(bytes)',
    `auto_face_cluster`   TINYINT       DEFAULT 0               COMMENT '是否自动人脸聚类（0-否 1-是）',
    `global_hide_location` TINYINT      DEFAULT 0               COMMENT '全局隐藏位置（0-否 1-是）',
    `status`              TINYINT       DEFAULT 1               COMMENT '状态（0-禁用 1-正常）',
    `create_time`         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at`          DATETIME      DEFAULT NULL            COMMENT '删除时间（逻辑删除）',
    `version`             INT           NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    KEY `idx_created_by` (`created_by`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='家庭表';

-- -----------------------------------------------------------
-- 家庭成员表
-- -----------------------------------------------------------
CREATE TABLE `t_family_member` (
    `id`                  BIGINT        NOT NULL                COMMENT '主键ID（雪花算法）',
    `family_id`           BIGINT        NOT NULL                COMMENT '家庭ID',
    `user_id`             BIGINT        NOT NULL                COMMENT '用户ID',
    `role`                VARCHAR(16)   DEFAULT 'member'        COMMENT '角色（admin/member）',
    `alias`               VARCHAR(32)   DEFAULT NULL            COMMENT '别名',
    `joined_via`          BIGINT        DEFAULT NULL            COMMENT '通过哪个邀请加入',
    `is_face_recognized`  TINYINT       DEFAULT 0               COMMENT '是否已人脸识别（0-否 1-是）',
    `status`              TINYINT       DEFAULT 1               COMMENT '状态（0-已移除 1-正常）',
    `join_time`           DATETIME      DEFAULT NULL            COMMENT '加入时间',
    `create_time`         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at`          DATETIME      DEFAULT NULL            COMMENT '删除时间（逻辑删除）',
    `version`             INT           NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_family_user` (`family_id`, `user_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='家庭成员表';

-- -----------------------------------------------------------
-- 邀请表
-- -----------------------------------------------------------
CREATE TABLE `t_invitation` (
    `id`           BIGINT        NOT NULL                COMMENT '主键ID（雪花算法）',
    `family_id`    BIGINT        NOT NULL                COMMENT '家庭ID',
    `inviter_id`   BIGINT        NOT NULL                COMMENT '邀请者ID',
    `token`        VARCHAR(64)   NOT NULL                COMMENT '邀请令牌',
    `expire_time`  DATETIME      NOT NULL                COMMENT '过期时间',
    `max_uses`     INT           DEFAULT NULL            COMMENT '最大使用次数',
    `use_count`    INT           DEFAULT 0               COMMENT '已使用次数',
    `status`       VARCHAR(16)   DEFAULT 'pending'       COMMENT '状态（pending/used/expired）',
    `create_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at`   DATETIME      DEFAULT NULL            COMMENT '删除时间（逻辑删除）',
    `version`      INT           NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_token` (`token`),
    KEY `idx_family_id` (`family_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='邀请表';
