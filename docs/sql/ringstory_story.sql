-- ============================================================
-- RingStory 故事数据库初始化脚本
-- Database: ringstory_story
-- ============================================================

CREATE DATABASE IF NOT EXISTS `ringstory_story` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `ringstory_story`;

-- -----------------------------------------------------------
-- 照片笔记表
-- -----------------------------------------------------------
CREATE TABLE `t_photo_note` (
    `id`              BIGINT        NOT NULL                COMMENT '主键ID（雪花算法）',
    `photo_id`        BIGINT        NOT NULL                COMMENT '照片ID',
    `author_id`       BIGINT        NOT NULL                COMMENT '作者ID',
    `content`         TEXT          DEFAULT NULL            COMMENT '笔记内容',
    `location_name`   VARCHAR(256)  DEFAULT NULL            COMMENT '位置名称',
    `latitude`        DOUBLE        DEFAULT NULL            COMMENT '纬度',
    `longitude`       DOUBLE        DEFAULT NULL            COMMENT '经度',
    `mentioned_users` VARCHAR(512)  DEFAULT NULL            COMMENT '@提及的用户ID列表（逗号分隔）',
    `create_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at`      DATETIME      DEFAULT NULL            COMMENT '删除时间（逻辑删除）',
    `version`         INT           NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    KEY `idx_photo_id` (`photo_id`),
    KEY `idx_author_id` (`author_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='照片笔记表';
