-- 读书笔记和日记记录模块数据库表结构
-- 创建时间: 2024年

-- 1. 读书笔记表
CREATE TABLE reading_notes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    book_title VARCHAR(255) NOT NULL COMMENT '书名',
    author VARCHAR(100) COMMENT '作者',
    isbn VARCHAR(20) COMMENT 'ISBN号',
    reading_status ENUM('未读', '在读', '已读', '已弃') DEFAULT '未读' COMMENT '阅读状态',
    start_date DATE COMMENT '开始阅读日期',
    finish_date DATE COMMENT '完成阅读日期',
    rating TINYINT CHECK (rating >= 1 AND rating <= 5) COMMENT '评分(1-5)',
    notes_content TEXT COMMENT '笔记内容',
    tags VARCHAR(500) COMMENT '标签，逗号分隔',
    cover_image VARCHAR(500) COMMENT '封面图片URL',
    page_count INT COMMENT '总页数',
    current_page INT DEFAULT 0 COMMENT '当前阅读页数',
    reading_time INT DEFAULT 0 COMMENT '阅读时长(分钟)',
    is_favorite BOOLEAN DEFAULT FALSE COMMENT '是否收藏',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_book_title (book_title),
    INDEX idx_reading_status (reading_status),
    INDEX idx_created_time (created_time)
) COMMENT='读书笔记表';

-- 2. 读书笔记详情表（用于记录每章的笔记）
CREATE TABLE reading_note_details (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    note_id BIGINT NOT NULL COMMENT '读书笔记ID',
    chapter_title VARCHAR(255) COMMENT '章节标题',
    page_start INT COMMENT '起始页',
    page_end INT COMMENT '结束页',
    content TEXT COMMENT '章节笔记内容',
    highlights TEXT COMMENT '重点标记',
    thoughts TEXT COMMENT '思考感悟',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (note_id) REFERENCES reading_notes(id) ON DELETE CASCADE,
    INDEX idx_note_id (note_id),
    INDEX idx_chapter_title (chapter_title)
) COMMENT='读书笔记详情表';

-- 3. 日记记录表
CREATE TABLE diary_entries (
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

-- 4. 日记附件表（用于存储图片、文件等）
CREATE TABLE diary_attachments (
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

-- 5. 阅读统计表
CREATE TABLE reading_statistics (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    year INT NOT NULL COMMENT '年份',
    month INT NOT NULL COMMENT '月份',
    books_read INT DEFAULT 0 COMMENT '本月已读书籍数',
    pages_read INT DEFAULT 0 COMMENT '本月已读页数',
    reading_time INT DEFAULT 0 COMMENT '本月阅读时长(分钟)',
    notes_count INT DEFAULT 0 COMMENT '本月笔记数',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_year_month (user_id, year, month),
    INDEX idx_user_id (user_id),
    INDEX idx_year_month (year, month)
) COMMENT='阅读统计表';

-- 6. 标签表
CREATE TABLE tags (
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
INSERT INTO tags (user_id, tag_name, tag_type, color) VALUES 
(1, '技术', 'reading', '#1890ff'),
(1, '文学', 'reading', '#52c41a'),
(1, '历史', 'reading', '#faad14'),
(1, '工作', 'diary', '#f5222d'),
(1, '生活', 'diary', '#722ed1'),
(1, '学习', 'diary', '#13c2c2'); 