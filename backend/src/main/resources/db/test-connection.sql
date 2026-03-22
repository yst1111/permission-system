-- 数据库连接测试脚本
-- 用于验证数据库连接和表结构

-- 1. 检查数据库是否存在
SELECT DATABASE() as current_database;

-- 2. 检查表是否存在
SHOW TABLES LIKE 'diary_entries';
SHOW TABLES LIKE 'reading_notes';
SHOW TABLES LIKE 'tags';

-- 3. 检查表结构
DESCRIBE diary_entries;
DESCRIBE reading_notes;
DESCRIBE tags;

-- 4. 检查示例数据
SELECT COUNT(*) as diary_count FROM diary_entries;
SELECT COUNT(*) as tags_count FROM tags;

-- 5. 测试查询
SELECT * FROM diary_entries LIMIT 3;
SELECT * FROM tags WHERE tag_type = 'diary' LIMIT 5; 