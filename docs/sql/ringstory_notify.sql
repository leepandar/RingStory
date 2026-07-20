-- ============================================================
-- RingStory 通知数据库初始化脚本
-- Database: ringstory_notify
-- ============================================================

CREATE DATABASE IF NOT EXISTS `ringstory_notify` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `ringstory_notify`;

-- -----------------------------------------------------------
-- 通知表
-- -----------------------------------------------------------
CREATE TABLE `t_notification` (
    `id`            BIGINT        NOT NULL                COMMENT '主键ID（雪花算法）',
    `recipient_id`  BIGINT        NOT NULL                COMMENT '接收者ID',
    `family_id`     BIGINT        DEFAULT NULL            COMMENT '家庭ID',
    `type`          VARCHAR(32)   NOT NULL                COMMENT '通知类型（new_photo/comment/like/invite等）',
    `title`         VARCHAR(128)  DEFAULT NULL            COMMENT '标题',
    `body`          VARCHAR(512)  DEFAULT NULL            COMMENT '内容',
    `is_read`       TINYINT       DEFAULT 0               COMMENT '是否已读（0-否 1-是）',
    `target_url`    VARCHAR(512)  DEFAULT NULL            COMMENT '跳转URL',
    `image_url`     VARCHAR(512)  DEFAULT NULL            COMMENT '图片URL',
    `create_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at`    DATETIME      DEFAULT NULL            COMMENT '删除时间（逻辑删除）',
    `version`       INT           NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    KEY `idx_recipient_id` (`recipient_id`, `is_read`),
    KEY `idx_family_id` (`family_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';
