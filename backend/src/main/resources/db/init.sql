-- 创建数据库
CREATE DATABASE IF NOT EXISTS permission_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE permission_system;

-- 用户表
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '用户表';

-- 角色表
CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(200) COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '角色表';

-- 菜单表
CREATE TABLE sys_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '菜单ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID',
    menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    menu_code VARCHAR(50) NOT NULL UNIQUE COMMENT '菜单编码',
    path VARCHAR(200) COMMENT '路由路径',
    component VARCHAR(200) COMMENT '组件路径',
    icon VARCHAR(100) COMMENT '图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    menu_type TINYINT DEFAULT 1 COMMENT '菜单类型：1-菜单，2-按钮',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '菜单表';

-- 用户角色关联表
CREATE TABLE sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_role (user_id, role_id)
) COMMENT '用户角色关联表';

-- 角色菜单关联表
CREATE TABLE sys_role_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_role_menu (role_id, menu_id)
) COMMENT '角色菜单关联表';

-- 省市区表
CREATE TABLE sys_region (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '地区ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父级ID',
    region_name VARCHAR(100) NOT NULL COMMENT '地区名称',
    region_code VARCHAR(20) NOT NULL UNIQUE COMMENT '地区编码',
    level TINYINT NOT NULL COMMENT '级别：1-省，2-市，3-区',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '省市区表';

-- 插入初始数据
INSERT INTO sys_user (username, password, nickname, email) VALUES 
('admin', '$2a$10$7JB720yubVSOfvVWdBYoOeymFvKd9l4QfGcKbFdcYpJYlnxHtM.SG', '系统管理员', 'admin@example.com');

INSERT INTO sys_role (role_name, role_code, description) VALUES 
('超级管理员', 'SUPER_ADMIN', '系统超级管理员'),
('普通用户', 'USER', '普通用户');

INSERT INTO sys_menu (parent_id, menu_name, menu_code, path, component, icon, sort_order, menu_type) VALUES 
(0, '系统管理', 'SYSTEM_MANAGE', '/system', 'Layout', 'setting', 1, 1),
(1, '用户管理', 'USER_MANAGE', '/system/user', 'system/user/index', 'user', 1, 1),
(1, '角色管理', 'ROLE_MANAGE', '/system/role', 'system/role/index', 'peoples', 2, 1),
(1, '菜单管理', 'MENU_MANAGE', '/system/menu', 'system/menu/index', 'tree-table', 3, 1),
(0, '地区管理', 'REGION_MANAGE', '/region', 'region/index', 'location', 2, 1);

-- 插入省市区示例数据
INSERT INTO sys_region (parent_id, region_name, region_code, level, sort_order) VALUES 
(0, '北京市', '110000', 1, 1),
(0, '天津市', '120000', 1, 2),
(0, '河北省', '130000', 1, 3),
(1, '北京市', '110100', 2, 1),
(2, '天津市', '120100', 2, 1),
(3, '石家庄市', '130100', 2, 1),
(4, '东城区', '110101', 3, 1),
(4, '西城区', '110102', 3, 2),
(5, '和平区', '120101', 3, 1),
(5, '河东区', '120102', 3, 2),
(6, '长安区', '130102', 3, 1),
(6, '桥西区', '130104', 3, 2);

-- 关联管理员角色和菜单
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES 
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5); 