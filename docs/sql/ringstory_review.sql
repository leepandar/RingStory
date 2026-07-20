-- ============================================================
-- RingStory 放映室数据库初始化脚本
-- Database: ringstory_review
-- ============================================================

CREATE DATABASE IF NOT EXISTS `ringstory_review` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `ringstory_review`;

-- -----------------------------------------------------------
-- 放映室回顾表
-- -----------------------------------------------------------
CREATE TABLE `t_moments_review` (
    `id`             BIGINT        NOT NULL                COMMENT '主键ID（雪花算法）',
    `family_id`      BIGINT        NOT NULL                COMMENT '家庭ID',
    `type`           VARCHAR(16)   NOT NULL                COMMENT '类型（monthly/seasonal/yearly）',
    `title`          VARCHAR(128)  DEFAULT NULL            COMMENT '标题',
    `cover_url`      VARCHAR(512)  DEFAULT NULL            COMMENT '封面URL',
    `resource_url`   VARCHAR(512)  DEFAULT NULL            COMMENT '资源URL',
    `resource_type`  VARCHAR(16)   DEFAULT NULL            COMMENT '资源类型（video/slideshow）',
    `photo_ids`      VARCHAR(1024) DEFAULT NULL            COMMENT '关联照片ID列表（逗号分隔）',
    `star_member_id` BIGINT        DEFAULT NULL            COMMENT '明星成员ID',
    `year_month`     VARCHAR(7)    DEFAULT NULL            COMMENT '年月标识（如2024-01）',
    `status`         TINYINT       DEFAULT 0               COMMENT '状态（0-生成中 1-已完成 2-失败）',
    `error_msg`      VARCHAR(512)  DEFAULT NULL            COMMENT '错误信息',
    `generated_at`   DATETIME      DEFAULT NULL            COMMENT '生成时间',
    `create_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at`     DATETIME      DEFAULT NULL            COMMENT '删除时间（逻辑删除）',
    `version`        INT           NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    KEY `idx_family_id` (`family_id`),
    KEY `idx_year_month` (`family_id`, `year_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='放映室回顾表';
