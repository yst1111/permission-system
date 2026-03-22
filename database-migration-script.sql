-- ========================================
-- 地区表字段修改脚本
-- 将 parent_id 改为 parent_code
-- ========================================

-- 1. 备份现有数据（建议在执行前先备份）
-- CREATE TABLE sys_region_backup AS SELECT * FROM sys_region;

-- 2. 添加新的 parent_code 字段
ALTER TABLE sys_region ADD COLUMN parent_code VARCHAR(20) DEFAULT '' COMMENT '父级编码' AFTER parent_id;

-- 3. 更新现有数据的 parent_code 字段
-- 根据现有的 parent_id 查找对应的 region_code 并设置到 parent_code
UPDATE sys_region r1 
SET parent_code = (
    SELECT r2.region_code 
    FROM sys_region r2 
    WHERE r2.id = r1.parent_id
)
WHERE r1.parent_id > 0;

-- 4. 将顶级地区（parent_id = 0）的 parent_code 设置为空字符串
UPDATE sys_region SET parent_code = '' WHERE parent_id = 0;

-- 5. 删除旧的 parent_id 字段
ALTER TABLE sys_region DROP COLUMN parent_id;

-- 6. 添加索引以提高查询性能
ALTER TABLE sys_region ADD INDEX idx_parent_code (parent_code);
ALTER TABLE sys_region ADD INDEX idx_region_code (region_code);

-- 7. 验证数据完整性
-- 检查是否有孤立的子地区（parent_code 指向不存在的地区编码）
SELECT r1.id, r1.region_name, r1.region_code, r1.parent_code
FROM sys_region r1
WHERE r1.parent_code != '' 
AND r1.parent_code NOT IN (
    SELECT r2.region_code 
    FROM sys_region r2
);

-- 8. 如果需要回滚，可以使用以下脚本：
/*
-- 回滚脚本
ALTER TABLE sys_region ADD COLUMN parent_id BIGINT DEFAULT 0 COMMENT '父级ID' AFTER id;

UPDATE sys_region r1 
SET parent_id = (
    SELECT r2.id 
    FROM sys_region r2 
    WHERE r2.region_code = r1.parent_code
)
WHERE r1.parent_code != '';

UPDATE sys_region SET parent_id = 0 WHERE parent_code = '';

ALTER TABLE sys_region DROP COLUMN parent_code;
ALTER TABLE sys_region DROP INDEX idx_parent_code;
*/

-- ========================================
-- 执行完成后的验证查询
-- ========================================

-- 查看表结构
DESCRIBE sys_region;

-- 查看数据示例
SELECT id, region_name, region_code, parent_code, level, sort_order, status 
FROM sys_region 
ORDER BY level, sort_order 
LIMIT 20;

-- 查看层级关系
SELECT 
    r1.region_name as '地区名称',
    r1.region_code as '地区编码',
    r1.level as '级别',
    CASE 
        WHEN r1.parent_code = '' THEN '顶级地区'
        ELSE CONCAT(r1.parent_code, ' - ', COALESCE(r2.region_name, '未知父级'))
    END as '父级关系'
FROM sys_region r1
LEFT JOIN sys_region r2 ON r1.parent_code = r2.region_code
ORDER BY r1.level, r1.sort_order; 