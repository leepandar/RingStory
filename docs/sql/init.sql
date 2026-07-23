-- ============================================================
-- RingStory 全量数据库初始化脚本
-- 包含所有微服务数据库及表结构
-- ============================================================

-- ************************************************************
-- 数据库: ringstory_user（用户服务 + 审计日志）
-- ************************************************************
CREATE DATABASE IF NOT EXISTS `ringstory_user` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `ringstory_user`;

-- 用户表
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

-- 审计日志表
CREATE TABLE `t_audit_log` (
    `id`             BIGINT        NOT NULL                COMMENT '主键ID（雪花算法）',
    `actor_id`       BIGINT        NOT NULL                COMMENT '操作者ID',
    `family_id`      BIGINT        DEFAULT NULL            COMMENT '家庭ID',
    `action`         VARCHAR(16)   NOT NULL                COMMENT '操作类型（CREATE/UPDATE/DELETE）',
    `target_table`   VARCHAR(64)   NOT NULL                COMMENT '目标表名',
    `target_id`      BIGINT        DEFAULT NULL            COMMENT '目标记录ID',
    `old_value`      JSON          DEFAULT NULL            COMMENT '旧值（JSON）',
    `new_value`      JSON          DEFAULT NULL            COMMENT '新值（JSON）',
    `ip_address`     VARCHAR(64)   DEFAULT NULL            COMMENT '操作IP',
    `user_agent`     VARCHAR(256)  DEFAULT NULL            COMMENT '客户端标识',
    `create_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_actor_id` (`actor_id`),
    KEY `idx_family_id` (`family_id`),
    KEY `idx_target` (`target_table`, `target_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审计日志表';

-- ************************************************************
-- 数据库: ringstory_family（家庭服务）
-- ************************************************************
CREATE DATABASE IF NOT EXISTS `ringstory_family` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `ringstory_family`;

-- 家庭表
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

-- 家庭成员表
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

-- 邀请表
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

-- ************************************************************
-- 数据库: ringstory_album（相册服务 + 标签 + 人脸聚类）
-- ************************************************************
CREATE DATABASE IF NOT EXISTS `ringstory_album` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `ringstory_album`;

-- 相册表
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

-- 照片表
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
    `status`             TINYINT       DEFAULT 1               COMMENT '状态（0-待审核 1-正常 2-处理中 3-违规拦截）',
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

-- 评论表
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

-- 点赞表
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

-- 照片影集关联表
CREATE TABLE `t_photo_album` (
    `id`           BIGINT        NOT NULL                COMMENT '主键ID（雪花算法）',
    `photo_id`     BIGINT        NOT NULL                COMMENT '照片ID',
    `album_id`     BIGINT        NOT NULL                COMMENT '影集ID',
    `sort_order`   INT           DEFAULT 0               COMMENT '排序序号',
    `added_by`     BIGINT        NOT NULL                COMMENT '添加者ID',
    `create_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at`   DATETIME      DEFAULT NULL            COMMENT '删除时间（逻辑删除）',
    `version`      INT           NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_photo_album` (`photo_id`, `album_id`),
    KEY `idx_album_id` (`album_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='照片影集关联表';

-- 标签定义表
CREATE TABLE `t_tag` (
    `id`           BIGINT        NOT NULL                COMMENT '主键ID（雪花算法）',
    `family_id`    BIGINT        NOT NULL                COMMENT '家庭ID',
    `name`         VARCHAR(32)   NOT NULL                COMMENT '标签名称',
    `created_by`   BIGINT        NOT NULL                COMMENT '创建者ID',
    `create_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at`   DATETIME      DEFAULT NULL            COMMENT '删除时间（逻辑删除）',
    `version`      INT           NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_family_name` (`family_id`, `name`),
    KEY `idx_family_id` (`family_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签定义表';

-- 照片标签关联表
CREATE TABLE `t_photo_tag` (
    `id`           BIGINT        NOT NULL                COMMENT '主键ID（雪花算法）',
    `photo_id`     BIGINT        NOT NULL                COMMENT '照片ID',
    `tag_id`       BIGINT        NOT NULL                COMMENT '标签ID',
    `create_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at`   DATETIME      DEFAULT NULL            COMMENT '删除时间（逻辑删除）',
    `version`      INT           NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_photo_tag` (`photo_id`, `tag_id`),
    KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='照片标签关联表';

-- 人脸聚类表
CREATE TABLE `t_face_cluster` (
    `id`              BIGINT        NOT NULL                COMMENT '主键ID（雪花算法）',
    `family_id`       BIGINT        NOT NULL                COMMENT '家庭ID',
    `name`            VARCHAR(32)   DEFAULT NULL            COMMENT '人物名称（未命名时为NULL）',
    `cover_photo_id`  BIGINT        DEFAULT NULL            COMMENT '封面照片ID（最佳人脸）',
    `status`          VARCHAR(16)   DEFAULT 'unnamed'       COMMENT '状态（unnamed-待命名/named-已命名）',
    `created_by`      BIGINT        DEFAULT NULL            COMMENT '命名操作者ID',
    `photo_count`     INT           DEFAULT 0               COMMENT '照片数量',
    `create_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at`      DATETIME      DEFAULT NULL            COMMENT '删除时间（逻辑删除）',
    `version`         INT           NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    KEY `idx_family_id` (`family_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='人脸聚类表';

-- 照片人脸标注表
CREATE TABLE `t_face_photo` (
    `id`               BIGINT        NOT NULL                COMMENT '主键ID（雪花算法）',
    `face_cluster_id`  BIGINT        NOT NULL                COMMENT '人脸聚类ID',
    `photo_id`         BIGINT        NOT NULL                COMMENT '照片ID',
    `face_coords`      VARCHAR(128)  DEFAULT NULL            COMMENT '人脸坐标JSON（{x,y,w,h}）',
    `confidence`       DOUBLE        DEFAULT NULL            COMMENT '置信度',
    `embedding`        TEXT          DEFAULT NULL            COMMENT '人脸特征向量（512维，逗号分隔）',
    `is_excluded`      TINYINT       DEFAULT 0               COMMENT '是否被手动排除（0-否 1-是）',
    `create_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at`       DATETIME      DEFAULT NULL            COMMENT '删除时间（逻辑删除）',
    `version`          INT           NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    KEY `idx_cluster_id` (`face_cluster_id`),
    KEY `idx_photo_id` (`photo_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='照片人脸标注表';

-- ************************************************************
-- 数据库: ringstory_notify（通知服务）
-- ************************************************************
CREATE DATABASE IF NOT EXISTS `ringstory_notify` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `ringstory_notify`;

-- 通知表
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

-- 通知偏好设置表
CREATE TABLE `t_notification_setting` (
    `id`             BIGINT        NOT NULL                COMMENT '主键ID（雪花算法）',
    `user_id`        BIGINT        NOT NULL                COMMENT '用户ID',
    `settings`       JSON          NOT NULL                COMMENT '通知偏好JSON',
    `create_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at`     DATETIME      DEFAULT NULL            COMMENT '删除时间（逻辑删除）',
    `version`        INT           NOT NULL DEFAULT 0      COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知偏好设置表';
-- settings JSON 结构示例：
-- {
--   "new_member":       { "enabled": true },
--   "photo_like":       { "enabled": true },
--   "new_comment":      { "enabled": true },
--   "review_complete":  { "enabled": true },
--   "photo_violation":  { "enabled": true },
--   "storage_warning":  { "enabled": true },
--   "quiet_hours":      { "enabled": false, "start": "22:00", "end": "08:00" },
--   "mute_all":         false
-- }

-- ************************************************************
-- 数据库: ringstory_review（放映室服务）
-- ************************************************************
CREATE DATABASE IF NOT EXISTS `ringstory_review` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `ringstory_review`;

-- 放映室回顾表
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

-- ************************************************************
-- 数据库: ringstory_story（故事服务）
-- ************************************************************
CREATE DATABASE IF NOT EXISTS `ringstory_story` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `ringstory_story`;

-- 照片笔记表
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

-- 备注版本历史表
CREATE TABLE `t_photo_note_history` (
    `id`           BIGINT        NOT NULL                COMMENT '主键ID（雪花算法）',
    `note_id`      BIGINT        NOT NULL                COMMENT '备注ID',
    `content`      TEXT          DEFAULT NULL            COMMENT '历史版本内容',
    `version`      INT           NOT NULL                COMMENT '版本号',
    `editor_id`    BIGINT        NOT NULL                COMMENT '编辑者ID',
    `create_time`  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_note_id` (`note_id`),
    KEY `idx_note_version` (`note_id`, `version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='备注版本历史表';
