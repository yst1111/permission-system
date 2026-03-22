-- 删除整个数据库的 SQL 脚本
-- 数据库名称: permission_system

-- 方法1: 直接删除数据库（推荐）
DROP DATABASE IF EXISTS permission_system;

-- 方法2: 如果方法1失败，先删除所有表，再删除数据库
-- 注意：这种方法需要先连接到数据库

-- 删除所有表（如果需要的话）
-- USE permission_system;
-- SET FOREIGN_KEY_CHECKS = 0;
-- 
-- -- 获取所有表名并删除
-- SELECT CONCAT('DROP TABLE IF EXISTS `', table_name, '`;') 
-- FROM information_schema.tables 
-- WHERE table_schema = 'permission_system';
-- 
-- SET FOREIGN_KEY_CHECKS = 1;
-- 
-- -- 最后删除数据库
-- DROP DATABASE IF EXISTS permission_system;

-- 方法3: 强制删除数据库（如果数据库被锁定）
-- DROP DATABASE permission_system FORCE;

-- 验证数据库是否已被删除
-- SHOW DATABASES LIKE 'permission_system'; 