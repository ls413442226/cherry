DROP TABLE IF EXISTS sys_login_log;
DROP TABLE IF EXISTS sys_refresh_token;
DROP TABLE IF EXISTS sys_user_device;
DROP TABLE IF EXISTS sys_user_role;
DROP TABLE IF EXISTS sys_role;
DROP TABLE IF EXISTS sys_user;

CREATE TABLE sys_user (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
                          user_no VARCHAR(32) NOT NULL UNIQUE COMMENT '用户编号(对外唯一)',
                          username VARCHAR(64) NOT NULL UNIQUE COMMENT '登录账号',
                          display_name VARCHAR(64) COMMENT '昵称',
                          password VARCHAR(255) NOT NULL COMMENT '加密密码',
                          email VARCHAR(128) UNIQUE COMMENT '邮箱',
                          phone VARCHAR(32) UNIQUE COMMENT '手机号',
                          avatar VARCHAR(255) COMMENT '头像URL',
                          gender TINYINT DEFAULT 0 COMMENT '0未知 1男 2女',
                          status TINYINT DEFAULT 1 COMMENT '1正常 2冻结 3禁用',
                          login_fail_count INT DEFAULT 0 COMMENT '登录失败次数',
                          last_login_ip VARCHAR(64),
                          last_login_time DATETIME,
                          register_source VARCHAR(32),
                          is_deleted TINYINT DEFAULT 0,
                          version INT DEFAULT 1,
                          created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                          updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='系统用户表';

CREATE TABLE sys_role (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
                          role_code VARCHAR(64) NOT NULL UNIQUE COMMENT '角色编码',
                          role_name VARCHAR(128) NOT NULL COMMENT '角色名称',
                          description VARCHAR(255),
                          status TINYINT DEFAULT 1,
                          is_deleted TINYINT DEFAULT 0,
                          created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                          updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='角色表';

CREATE TABLE sys_user_role (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               user_id BIGINT NOT NULL,
                               role_id BIGINT NOT NULL,
                               created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                               UNIQUE KEY uk_user_role (user_id, role_id)
) COMMENT='用户角色关系表';

CREATE TABLE sys_user_device (
                                 id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                 user_id BIGINT NOT NULL,
                                 device_id VARCHAR(128) NOT NULL,
                                 device_type VARCHAR(32),
                                 device_name VARCHAR(128),
                                 ip_address VARCHAR(64),
                                 last_login_time DATETIME,
                                 status TINYINT DEFAULT 1,
                                 is_deleted TINYINT DEFAULT 0,
                                 created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                 UNIQUE KEY uk_user_device (user_id, device_id)
) COMMENT='用户设备表';

CREATE TABLE sys_refresh_token (
                                   id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                   user_id BIGINT NOT NULL,
                                   device_id VARCHAR(128) NOT NULL,
                                   refresh_token VARCHAR(255) NOT NULL,
                                   expire_at DATETIME NOT NULL,
                                   is_revoked TINYINT DEFAULT 0,
                                   created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                                   UNIQUE KEY uk_refresh (user_id, device_id)
) COMMENT='刷新令牌表';

CREATE TABLE sys_login_log (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               user_id BIGINT,
                               username VARCHAR(64),
                               device_id VARCHAR(128),
                               device_type VARCHAR(32),
                               ip_address VARCHAR(64),
                               login_status TINYINT,
                               fail_reason VARCHAR(255),
                               created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT='登录日志表';

INSERT INTO sys_role (id, role_code, role_name) VALUES
                                                    (1, 'SUPER_ADMIN', '超级管理员'),
                                                    (2, 'ADMIN', '管理员'),
                                                    (3, 'USER', '普通用户');

INSERT INTO sys_user
(id, user_no, username, display_name, password, email, phone, status)
VALUES
    (1, 'U20260001', 'superadmin', '超级管理员',
     '$2a$10$7QJ1z0HqHjYq2uZqzJYh9eBzL3dP0p9b5Jg6b3E7hJz6nQyGkX8yK',
     'super@cherry.com','13800000001',1),

    (2, 'U20260002', 'admin', '管理员',
     '$2a$10$7QJ1z0HqHjYq2uZqzJYh9eBzL3dP0p9b5Jg6b3E7hJz6nQyGkX8yK',
     'admin@cherry.com','13800000002',1),

    (3, 'U20260003', 'user01', '张三',
     '$2a$10$7QJ1z0HqHjYq2uZqzJYh9eBzL3dP0p9b5Jg6b3E7hJz6nQyGkX8yK',
     'user01@cherry.com','13800000003',1);

INSERT INTO sys_user_role (user_id, role_id) VALUES
                                                 (1,1),
                                                 (2,2),
                                                 (3,3);

INSERT INTO sys_user_device
(user_id, device_id, device_type, device_name, ip_address, status)
VALUES
    (1,'web-001','WEB','Chrome','127.0.0.1',1),
    (2,'web-002','WEB','Edge','127.0.0.1',1),
    (3,'android-001','ANDROID','Pixel','192.168.1.20',1);
