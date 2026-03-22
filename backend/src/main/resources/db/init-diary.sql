-- 日记模块数据库初始化脚本
-- 如果表不存在则创建

-- 1. 日记记录表
CREATE TABLE IF NOT EXISTS diary_entries (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    entry_date DATE NOT NULL COMMENT '日记日期',
    title VARCHAR(255) COMMENT '日记标题',
    content TEXT NOT NULL COMMENT '日记内容',
    mood ENUM('开心', '平静', '难过', '愤怒', '兴奋', '焦虑', '其他') DEFAULT '平静' COMMENT '心情状态',
    weather VARCHAR(50) COMMENT '天气',
    location VARCHAR(255) COMMENT '地点',
    tags VARCHAR(500) COMMENT '标签，逗号分隔',
    is_private BOOLEAN DEFAULT TRUE COMMENT '是否私密',
    is_favorite BOOLEAN DEFAULT FALSE COMMENT '是否收藏',
    word_count INT DEFAULT 0 COMMENT '字数统计',
    reading_time INT DEFAULT 0 COMMENT '阅读时长(分钟)',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_entry_date (entry_date),
    INDEX idx_mood (mood),
    INDEX idx_created_time (created_time),
    UNIQUE KEY uk_user_date (user_id, entry_date)
) COMMENT='日记记录表';

-- 2. 日记附件表
CREATE TABLE IF NOT EXISTS diary_attachments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    diary_id BIGINT NOT NULL COMMENT '日记ID',
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    file_path VARCHAR(500) NOT NULL COMMENT '文件路径',
    file_type VARCHAR(50) COMMENT '文件类型',
    file_size BIGINT COMMENT '文件大小(字节)',
    upload_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    FOREIGN KEY (diary_id) REFERENCES diary_entries(id) ON DELETE CASCADE,
    INDEX idx_diary_id (diary_id),
    INDEX idx_file_type (file_type)
) COMMENT='日记附件表';

-- 3. 标签表
CREATE TABLE IF NOT EXISTS tags (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    tag_name VARCHAR(100) NOT NULL COMMENT '标签名称',
    tag_type ENUM('reading', 'diary') NOT NULL COMMENT '标签类型：reading-读书笔记，diary-日记',
    color VARCHAR(7) DEFAULT '#1890ff' COMMENT '标签颜色',
    use_count INT DEFAULT 0 COMMENT '使用次数',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_tag_type (user_id, tag_name, tag_type),
    INDEX idx_user_id (user_id),
    INDEX idx_tag_type (tag_type)
) COMMENT='标签表';

-- 插入示例数据
INSERT IGNORE INTO tags (user_id, tag_name, tag_type, color) VALUES 
(1, '技术', 'reading', '#1890ff'),
(1, '文学', 'reading', '#52c41a'),
(1, '历史', 'reading', '#faad14'),
(1, '工作', 'diary', '#f5222d'),
(1, '生活', 'diary', '#722ed1'),
(1, '学习', 'diary', '#13c2c2'),
(1, '朋友', 'diary', '#eb2f96'),
(1, '旅行', 'diary', '#fa8c16'),
(1, '美食', 'diary', '#a0d911');

-- 插入示例日记数据
INSERT IGNORE INTO diary_entries (user_id, entry_date, title, content, mood, weather, location, tags, is_private, is_favorite, word_count, reading_time) VALUES 
(1, '2024-01-15', '美好的一天', '今天天气很好，心情也很愉快。早上去了公园散步，看到了很多人在晨练，感受到了生活的美好。下午在家里看书，学到了很多新知识。晚上和朋友一起吃饭，聊得很开心。', '开心', '晴天', '家里', '生活,朋友,学习', false, true, 89, 1),
(1, '2024-01-14', '工作感悟', '今天在工作中遇到了一些挑战，但是通过团队合作，我们成功地解决了问题。这让我深刻体会到团队协作的重要性，也学到了很多新的技能。', '平静', '阴天', '公司', '工作,团队,学习', true, false, 76, 1),
(1, '2024-01-13', '学习笔记', '今天学习了Spring Boot框架，了解了依赖注入、AOP等核心概念。通过实践项目，加深了对这些概念的理解。明天继续深入学习。', '兴奋', '晴天', '家里', '学习,技术,Spring', false, false, 65, 1),
(1, '2024-01-12', '心情日记', '今天心情有些低落，可能是因为工作压力比较大。但是晚上和家人一起吃饭，聊了很多开心的事情，心情好了很多。', '难过', '雨天', '家里', '心情,家庭,工作', true, false, 58, 1); 