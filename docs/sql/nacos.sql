-- ============================================================
-- Nacos 2.5.1 MySQL 初始化脚本
-- 来源: https://github.com/alibaba/nacos/blob/2.5.1/distribution/conf/mysql-schema.sql
-- 使用方式: 创建 nacos 数据库后执行此脚本
-- ============================================================

CREATE DATABASE IF NOT EXISTS `ringstory_nacos` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `ringstory_nacos`;

/******************************************/
/*   表名称 = config_info                  */
/******************************************/
CREATE TABLE `config_info` (
    `id`                   BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT 'id',
    `data_id`              VARCHAR(255) NOT NULL COMMENT 'data_id',
    `group_id`             VARCHAR(128) DEFAULT NULL COMMENT 'group_id',
    `content`              LONGTEXT     NOT NULL COMMENT 'content',
    `md5`                  VARCHAR(32)  DEFAULT NULL COMMENT 'md5',
    `gmt_create`           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `src_user`             TEXT         COMMENT 'source user',
    `src_ip`               VARCHAR(50)  DEFAULT NULL COMMENT 'source ip',
    `app_name`             VARCHAR(128) DEFAULT NULL COMMENT 'app_name',
    `tenant_id`            VARCHAR(128) DEFAULT '' COMMENT '租户字段',
    `c_desc`               VARCHAR(256) DEFAULT NULL COMMENT 'configuration description',
    `c_use`                VARCHAR(64)  DEFAULT NULL COMMENT 'configuration usage',
    `effect`               VARCHAR(64)  DEFAULT NULL COMMENT '配置生效的描述',
    `type`                 VARCHAR(64)  DEFAULT NULL COMMENT '配置的类型',
    `c_schema`             TEXT         COMMENT '配置的模式',
    `encrypted_data_key`   VARCHAR(1024) NOT NULL DEFAULT '' COMMENT '密钥',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_configinfo_datagrouptenant` (`data_id`, `group_id`, `tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info';

/******************************************/
/*   表名称 = config_info_gray  since 2.5.0 */
/******************************************/
CREATE TABLE `config_info_gray` (
    `id`                   BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
    `data_id`              VARCHAR(255) NOT NULL COMMENT 'data_id',
    `group_id`             VARCHAR(128) NOT NULL COMMENT 'group_id',
    `content`              LONGTEXT     NOT NULL COMMENT 'content',
    `md5`                  VARCHAR(32)  DEFAULT NULL COMMENT 'md5',
    `src_user`             TEXT         COMMENT 'src_user',
    `src_ip`               VARCHAR(100) DEFAULT NULL COMMENT 'src_ip',
    `gmt_create`           DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'gmt_create',
    `gmt_modified`         DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT 'gmt_modified',
    `app_name`             VARCHAR(128) DEFAULT NULL COMMENT 'app_name',
    `tenant_id`            VARCHAR(128) DEFAULT '' COMMENT 'tenant_id',
    `gray_name`            VARCHAR(128) NOT NULL COMMENT 'gray_name',
    `gray_rule`            TEXT         NOT NULL COMMENT 'gray_rule',
    `encrypted_data_key`   VARCHAR(256) NOT NULL DEFAULT '' COMMENT 'encrypted_data_key',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_configinfogray_datagrouptenantgray` (`data_id`, `group_id`, `tenant_id`, `gray_name`),
    KEY `idx_dataid_gmt_modified` (`data_id`, `gmt_modified`),
    KEY `idx_gmt_modified` (`gmt_modified`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='config_info_gray';

/******************************************/
/*   表名称 = config_tags_relation         */
/******************************************/
CREATE TABLE `config_tags_relation` (
    `id`          BIGINT(20)   NOT NULL COMMENT 'id',
    `tag_name`    VARCHAR(128) NOT NULL COMMENT 'tag_name',
    `tag_type`    VARCHAR(64)  DEFAULT NULL COMMENT 'tag_type',
    `data_id`     VARCHAR(255) NOT NULL COMMENT 'data_id',
    `group_id`    VARCHAR(128) NOT NULL COMMENT 'group_id',
    `tenant_id`   VARCHAR(128) DEFAULT '' COMMENT 'tenant_id',
    `nid`         BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT 'nid, 自增长标识',
    PRIMARY KEY (`nid`),
    UNIQUE KEY `uk_configtagrelation_configidtag` (`id`, `tag_name`, `tag_type`),
    KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_tag_relation';

/******************************************/
/*   表名称 = group_capacity               */
/******************************************/
CREATE TABLE `group_capacity` (
    `id`                BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `group_id`          VARCHAR(128) NOT NULL DEFAULT '' COMMENT 'Group ID，空字符表示整个集群',
    `quota`             INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '配额，0表示使用默认值',
    `usage`             INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '使用量',
    `max_size`          INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
    `max_aggr_count`    INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '聚合子配置最大个数，0表示使用默认值',
    `max_aggr_size`     INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
    `max_history_count` INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '最大变更历史数量',
    `gmt_create`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_group_id` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='集群、各Group容量信息表';

/******************************************/
/*   表名称 = his_config_info              */
/******************************************/
CREATE TABLE `his_config_info` (
    `id`                   BIGINT(20) UNSIGNED NOT NULL COMMENT 'id',
    `nid`                  BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'nid, 自增标识',
    `data_id`              VARCHAR(255) NOT NULL COMMENT 'data_id',
    `group_id`             VARCHAR(128) NOT NULL COMMENT 'group_id',
    `app_name`             VARCHAR(128) DEFAULT NULL COMMENT 'app_name',
    `content`              LONGTEXT     NOT NULL COMMENT 'content',
    `md5`                  VARCHAR(32)  DEFAULT NULL COMMENT 'md5',
    `gmt_create`           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `src_user`             TEXT         COMMENT 'source user',
    `src_ip`               VARCHAR(50)  DEFAULT NULL COMMENT 'source ip',
    `op_type`              CHAR(10)     DEFAULT NULL COMMENT 'operation type',
    `tenant_id`            VARCHAR(128) DEFAULT '' COMMENT '租户字段',
    `encrypted_data_key`   VARCHAR(1024) NOT NULL DEFAULT '' COMMENT '密钥',
    `publish_type`         VARCHAR(50)  DEFAULT 'formal' COMMENT 'publish type gray or formal',
    `gray_name`            VARCHAR(50)  DEFAULT NULL COMMENT 'gray name',
    `ext_info`             LONGTEXT     DEFAULT NULL COMMENT 'ext info',
    PRIMARY KEY (`nid`),
    KEY `idx_gmt_create` (`gmt_create`),
    KEY `idx_gmt_modified` (`gmt_modified`),
    KEY `idx_did` (`data_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='多租户改造';

/******************************************/
/*   表名称 = tenant_capacity              */
/******************************************/
CREATE TABLE `tenant_capacity` (
    `id`                BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `tenant_id`         VARCHAR(128) NOT NULL DEFAULT '' COMMENT 'Tenant ID',
    `quota`             INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '配额，0表示使用默认值',
    `usage`             INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '使用量',
    `max_size`          INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
    `max_aggr_count`    INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '聚合子配置最大个数',
    `max_aggr_size`     INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
    `max_history_count` INT(10) UNSIGNED NOT NULL DEFAULT '0' COMMENT '最大变更历史数量',
    `gmt_create`        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='租户容量信息表';

/******************************************/
/*   表名称 = tenant_info                  */
/******************************************/
CREATE TABLE `tenant_info` (
    `id`            BIGINT(20)   NOT NULL AUTO_INCREMENT COMMENT 'id',
    `kp`            VARCHAR(128) NOT NULL COMMENT 'kp',
    `tenant_id`     VARCHAR(128) DEFAULT '' COMMENT 'tenant_id',
    `tenant_name`   VARCHAR(128) DEFAULT '' COMMENT 'tenant_name',
    `tenant_desc`   VARCHAR(256) DEFAULT NULL COMMENT 'tenant_desc',
    `create_source` VARCHAR(32)  DEFAULT NULL COMMENT 'create_source',
    `gmt_create`    BIGINT(20)   NOT NULL COMMENT '创建时间',
    `gmt_modified`  BIGINT(20)   NOT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_info_kptenantid` (`kp`, `tenant_id`),
    KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='tenant_info';

/******************************************/
/*   表名称 = users                        */
/******************************************/
CREATE TABLE `users` (
    `username` VARCHAR(50)  NOT NULL PRIMARY KEY COMMENT 'username',
    `password` VARCHAR(500) NOT NULL COMMENT 'password',
    `enabled`  BOOLEAN      NOT NULL COMMENT 'enabled'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='users';

/******************************************/
/*   表名称 = roles                        */
/******************************************/
CREATE TABLE `roles` (
    `username` VARCHAR(50) NOT NULL COMMENT 'username',
    `role`     VARCHAR(50) NOT NULL COMMENT 'role',
    UNIQUE INDEX `idx_user_role` (`username` ASC, `role` ASC) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='roles';

/******************************************/
/*   表名称 = permissions                  */
/******************************************/
CREATE TABLE `permissions` (
    `role`     VARCHAR(50)  NOT NULL COMMENT 'role',
    `resource` VARCHAR(128) NOT NULL COMMENT 'resource',
    `action`   VARCHAR(8)   NOT NULL COMMENT 'action',
    UNIQUE INDEX `uk_role_permission` (`role`, `resource`, `action`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='permissions';

-- ============================================================
-- 初始数据：默认管理员账号 nacos/nacos
-- ============================================================
INSERT INTO `users` (`username`, `password`, `enabled`) VALUES
('nacos', '$2a$10$sQtOkERXqHHNVfThOiZaDO4ZHbku/hxLPXJm7E1jhWoToNFEJZk.m', TRUE);

INSERT INTO `roles` (`username`, `role`) VALUES
('nacos', 'ROLE_ADMIN');
