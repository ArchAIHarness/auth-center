-- auth-center DDL

CREATE TABLE IF NOT EXISTS `t_access_token` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `modify_time` DATETIME DEFAULT NULL COMMENT '修改时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除，0否1是',
    `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `token_id` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '访问令牌ID',
    `user_id` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '用户ID',
    `issuer_id` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '签发者ID',
    `token_type` TINYINT(4) NOT NULL DEFAULT 0 COMMENT '令牌类型：0ACCESS/1REFRESH',
    `auth_type` TINYINT(4) NOT NULL DEFAULT 0 COMMENT '认证类型：0AKSK/1OAUTH/2SSO',
    `scopes` VARCHAR(1024) NOT NULL DEFAULT '' COMMENT '授权范围',
    `secret` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '签名密钥',
    `token_create_time` DATETIME DEFAULT NULL COMMENT '令牌创建时间',
    `token_expire_in_seconds` BIGINT NOT NULL DEFAULT 0 COMMENT '访问令牌有效期（秒）',
    `refresh_expire_in_seconds` BIGINT NOT NULL DEFAULT 0 COMMENT '刷新令牌有效期（秒）',
    `status` TINYINT(4) NOT NULL DEFAULT 0 COMMENT '令牌状态：0active/1expired/2revoked',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_token_id` (`token_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='访问令牌表';

CREATE TABLE IF NOT EXISTS `t_auth_code` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `modify_time` DATETIME DEFAULT NULL COMMENT '修改时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除，0否1是',
    `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `code` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '授权码',
    `user_id` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '用户ID',
    `client_id` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '客户端ID',
    `auth_create_time` DATETIME DEFAULT NULL COMMENT '授权码创建时间',
    `expire_in_seconds` INT NOT NULL DEFAULT 0 COMMENT '授权码有效期（秒）',
    `status` TINYINT(4) NOT NULL DEFAULT 0 COMMENT '授权码状态：0active/1used/2expired',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OAuth授权码表';