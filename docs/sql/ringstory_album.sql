-- ============================================================
-- RingStory 相册数据库初始化脚本
-- Database: ringstory_album
-- ============================================================

CREATE DATABASE IF NOT EXISTS `ringstory_album` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `ringstory_album`;

-- -----------------------------------------------------------
-- 相册表
-- -----------------------------------------------------------
CREATE TABLE `t_album` (
    `id`                   BIGINT        NOT NULL                COMMENT '主键ID（雪花算法）',
    `family_id`            BIGINT        NOT NULL                COMMENT '家庭ID',
    `name`                 VARCHAR(64)   NOT NULL                COMMENT '相册名称',
    `cover_photo_id`       BIGINT        DEFAULT NULL            COMMENT '封面照片ID',
    `creator_id`           BIGINT        NOT NULL                COMMENT '创建者ID',
    `allow_member_upload`  TINYINT       DEFAULT 1               COMMENT '是否允许成员上传（0-否 1-是）',
    `photo_count`          INT           DEFAULT 0               COMMENT '照片数量',
    `create_time`          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at`           DATETIME      DEFAULT NULL            COMMENT '删除时间（逻辑删除）',
    `version`              INT           NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    KEY `idx_family_id` (`family_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='相册表';

-- -----------------------------------------------------------
-- 照片表
-- -----------------------------------------------------------
CREATE TABLE `t_photo` (
    `id`                 BIGINT        NOT NULL                COMMENT '主键ID（雪花算法）',
    `family_id`          BIGINT        NOT NULL                COMMENT '家庭ID',
    `uploader_id`        BIGINT        NOT NULL                COMMENT '上传者ID',
    `oss_key`            VARCHAR(256)  NOT NULL                COMMENT 'OSS存储键',
    `original_name`      VARCHAR(256)  DEFAULT NULL            COMMENT '原始文件名',
    `md5`                VARCHAR(32)   DEFAULT NULL            COMMENT '文件MD5',
    `format`             VARCHAR(16)   DEFAULT NULL            COMMENT '文件格式（jpg/png/heic等）',
    `width`              INT           DEFAULT NULL            COMMENT '宽度(px)',
    `height`             INT           DEFAULT NULL            COMMENT '高度(px)',
    `file_size`          BIGINT        DEFAULT NULL            COMMENT '文件大小(bytes)',
    `shoot_time`         DATETIME      DEFAULT NULL            COMMENT '拍摄时间',
    `upload_time`        DATETIME      DEFAULT NULL            COMMENT '上传时间',
    `latitude`           DOUBLE        DEFAULT NULL            COMMENT '纬度',
    `longitude`          DOUBLE        DEFAULT NULL            COMMENT '经度',
    `location_name`      VARCHAR(256)  DEFAULT NULL            COMMENT '位置名称',
    `is_hidden_location` TINYINT       DEFAULT 0               COMMENT '是否隐藏位置（0-否 1-是）',
    `blur_hash`          VARCHAR(64)   DEFAULT NULL            COMMENT '模糊哈希',
    `status`             TINYINT       DEFAULT 1               COMMENT '状态（0-待审核 1-正常 2-已删除）',
    `is_favorite`        TINYINT       DEFAULT 0               COMMENT '是否收藏（0-否 1-是）',
    `like_count`         INT           DEFAULT 0               COMMENT '点赞数',
    `comment_count`      INT           DEFAULT 0               COMMENT '评论数',
    `create_time`        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at`         DATETIME      DEFAULT NULL            COMMENT '删除时间（逻辑删除）',
    `version`            INT           NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    KEY `idx_family_id` (`family_id`),
    KEY `idx_uploader_id` (`uploader_id`),
    KEY `idx_shoot_time` (`family_id`, `shoot_time`),
    KEY `idx_md5` (`md5`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='照片表';

-- -----------------------------------------------------------
-- 评论表
-- -----------------------------------------------------------
CREATE TABLE `t_comment` (
    `id`           BIGINT        NOT NULL                COMMENT '主键ID（雪花算法）',
    `photo_id`     BIGINT        NOT NULL                COMMENT '照片ID',
    `author_id`    BIGINT        NOT NULL                COMMENT '评论者ID',
    `parent_id`    BIGINT        DEFAULT NULL            COMMENT '父评论ID（NULL为顶级评论）',
    `content`      VARCHAR(512)  NOT NULL                COMMENT '评论内容',
    `like_count`   INT           DEFAULT 0               COMMENT '点赞数',
    `create_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at`   DATETIME      DEFAULT NULL            COMMENT '删除时间（逻辑删除）',
    `version`      INT           NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    KEY `idx_photo_id` (`photo_id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- -----------------------------------------------------------
-- 点赞表
-- -----------------------------------------------------------
CREATE TABLE `t_like` (
    `id`           BIGINT        NOT NULL                COMMENT '主键ID（雪花算法）',
    `photo_id`     BIGINT        NOT NULL                COMMENT '照片ID',
    `user_id`      BIGINT        NOT NULL                COMMENT '用户ID',
    `create_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at`   DATETIME      DEFAULT NULL            COMMENT '删除时间（逻辑删除）',
    `version`      INT           NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_photo_user` (`photo_id`, `user_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞表';
