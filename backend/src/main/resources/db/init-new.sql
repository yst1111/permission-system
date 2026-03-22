-- ========================================
-- 地区管理数据库初始化脚本（新版本）
-- 使用 parent_code 字段替代 parent_id 字段
-- ========================================

-- 省市区表
CREATE TABLE sys_region (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '地区ID',
    parent_code VARCHAR(20) DEFAULT '' COMMENT '父级编码',
    region_name VARCHAR(100) NOT NULL COMMENT '地区名称',
    region_code VARCHAR(20) NOT NULL UNIQUE COMMENT '地区编码',
    level TINYINT NOT NULL COMMENT '级别：1-省，2-市，3-区',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_parent_code (parent_code),
    INDEX idx_region_code (region_code)
) COMMENT '省市区表';

-- 插入省市区示例数据
INSERT INTO sys_region (parent_code, region_name, region_code, level, sort_order) VALUES 
('', '北京市', '110000', 1, 1),
('', '天津市', '120000', 1, 2),
('', '河北省', '130000', 1, 3),
('110000', '北京市', '110100', 2, 1),
('120000', '天津市', '120100', 2, 1),
('130000', '石家庄市', '130100', 2, 1),
('110100', '东城区', '110101', 3, 1),
('110100', '西城区', '110102', 3, 2),
('120100', '和平区', '120101', 3, 1),
('120100', '河东区', '120102', 3, 2),
('130100', '长安区', '130102', 3, 1),
('130100', '桥西区', '130104', 3, 2);

-- 验证数据完整性
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