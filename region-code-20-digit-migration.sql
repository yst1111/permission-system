-- ========================================
-- 地区编码字段扩展迁移脚本
-- 将地区编码从6位数字扩展到20位数字
-- ========================================

-- 1. 备份现有数据（建议在执行前先备份）
-- CREATE TABLE sys_region_backup AS SELECT * FROM sys_region;

-- 2. 检查当前表结构
DESCRIBE sys_region;

-- 3. 修改地区编码字段长度（如果当前不是VARCHAR(20)）
-- 注意：如果已经是VARCHAR(20)，则无需执行此步骤
ALTER TABLE sys_region MODIFY COLUMN region_code VARCHAR(20) NOT NULL COMMENT '地区编码';

-- 4. 修改父级编码字段长度（确保一致性）
ALTER TABLE sys_region MODIFY COLUMN parent_code VARCHAR(20) DEFAULT '' COMMENT '父级编码';

-- 5. 验证字段长度修改
DESCRIBE sys_region;

-- 6. 检查现有数据完整性
SELECT 
    region_code,
    LENGTH(region_code) as code_length,
    region_name,
    level
FROM sys_region 
ORDER BY level, region_code;

-- 7. 验证索引是否正常
SHOW INDEX FROM sys_region;

-- 8. 测试插入20位数字的地区编码
-- INSERT INTO sys_region (parent_code, region_name, region_code, level, sort_order) VALUES 
-- ('', '测试省份', '12345678901234567890', 1, 999);

-- 9. 如果需要回滚，可以使用以下脚本：
/*
-- 回滚脚本（将字段长度改回6位）
ALTER TABLE sys_region MODIFY COLUMN region_code VARCHAR(6) NOT NULL COMMENT '地区编码';
ALTER TABLE sys_region MODIFY COLUMN parent_code VARCHAR(6) DEFAULT '' COMMENT '父级编码';

-- 注意：回滚前请确保没有超过6位的数据
SELECT region_code, LENGTH(region_code) as code_length
FROM sys_region 
WHERE LENGTH(region_code) > 6;
*/

-- ========================================
-- 执行完成后的验证查询
-- ========================================

-- 验证字段长度
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    CHARACTER_MAXIMUM_LENGTH,
    IS_NULLABLE,
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
AND TABLE_NAME = 'sys_region'
AND COLUMN_NAME IN ('region_code', 'parent_code');

-- 验证数据完整性
SELECT 
    COUNT(*) as total_regions,
    COUNT(CASE WHEN LENGTH(region_code) <= 6 THEN 1 END) as standard_length_codes,
    COUNT(CASE WHEN LENGTH(region_code) > 6 THEN 1 END) as extended_length_codes,
    MAX(LENGTH(region_code)) as max_code_length,
    MIN(LENGTH(region_code)) as min_code_length
FROM sys_region; 