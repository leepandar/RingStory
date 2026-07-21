-- ============================================================
-- RingStory 用户数据库初始化脚本
-- Database: ringstory_user
-- ============================================================

CREATE DATABASE IF NOT EXISTS `ringstory_user` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `ringstory_user`;

-- -----------------------------------------------------------
-- 用户表
-- -----------------------------------------------------------
CREATE TABLE `t_user` (
    `id`               BIGINT        NOT NULL                COMMENT '主键ID（雪花算法）',
    `open_id`          VARCHAR(64)   DEFAULT NULL            COMMENT '微信openId',
    `username`         VARCHAR(32)   DEFAULT NULL            COMMENT '管理后台用户名',
    `password`         VARCHAR(128)  DEFAULT NULL            COMMENT '管理后台密码（BCrypt加密）',
    `union_id`         VARCHAR(64)   DEFAULT NULL            COMMENT '微信unionId',
    `nick_name`        VARCHAR(64)   DEFAULT NULL            COMMENT '昵称',
    `avatar_url`       VARCHAR(512)  DEFAULT NULL            COMMENT '头像URL',
    `phone`            VARCHAR(20)   DEFAULT NULL            COMMENT '手机号',
    `gender`           TINYINT       DEFAULT 0               COMMENT '性别（0-未知 1-男 2-女）',
    `status`           TINYINT       DEFAULT 1               COMMENT '状态（0-禁用 1-正常）',
    `preferences`      JSON          DEFAULT NULL            COMMENT '用户偏好设置（JSON）',
    `last_active_time` DATETIME      DEFAULT NULL            COMMENT '最后活跃时间',
    `create_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at`       DATETIME      DEFAULT NULL            COMMENT '删除时间（逻辑删除）',
    `version`          INT           NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_open_id` (`open_id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_union_id` (`union_id`),
    KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
