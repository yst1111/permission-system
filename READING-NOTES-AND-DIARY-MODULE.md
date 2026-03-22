# 读书笔记和日记记录模块

## 项目概述

本项目为权限管理系统新增了读书笔记和日记记录两个功能模块，提供完整的个人知识管理和生活记录功能。

## 功能特性

### 📚 读书笔记模块
- **书籍管理**: 添加、编辑、删除读书笔记
- **阅读进度**: 记录当前阅读页数和阅读时长
- **状态跟踪**: 未读、在读、已读、已弃四种状态
- **评分系统**: 1-5星评分机制
- **标签分类**: 支持自定义标签分类
- **搜索功能**: 按书名、作者、内容、标签搜索
- **收藏功能**: 标记重要书籍
- **统计报表**: 阅读数据统计和分析

### 📝 日记记录模块
- **日记管理**: 创建、编辑、删除日记
- **心情记录**: 记录每日心情状态
- **天气地点**: 记录天气和地点信息
- **标签系统**: 支持多标签分类
- **隐私控制**: 支持私密日记设置
- **日历视图**: 日历形式查看日记
- **搜索筛选**: 按内容、心情、日期范围搜索
- **字数统计**: 自动统计日记字数

## 技术架构

### 后端技术栈
- **框架**: Spring Boot 2.5.14
- **数据库**: MySQL 8.0
- **ORM**: MyBatis
- **验证**: Spring Validation
- **文档**: Swagger 2
- **构建**: Maven

### 前端技术栈
- **框架**: Vue 3 + Element Plus
- **路由**: Vue Router 4
- **构建**: Vite
- **样式**: CSS3 + Element Plus UI

## 数据库设计

### 核心表结构

#### 1. 读书笔记表 (reading_notes)
```sql
CREATE TABLE reading_notes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    book_title VARCHAR(255) NOT NULL,
    author VARCHAR(100),
    isbn VARCHAR(20),
    reading_status ENUM('未读', '在读', '已读', '已弃'),
    start_date DATE,
    finish_date DATE,
    rating TINYINT CHECK (rating >= 1 AND rating <= 5),
    notes_content TEXT,
    tags VARCHAR(500),
    cover_image VARCHAR(500),
    page_count INT,
    current_page INT DEFAULT 0,
    reading_time INT DEFAULT 0,
    is_favorite BOOLEAN DEFAULT FALSE,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### 2. 日记记录表 (diary_entries)
```sql
CREATE TABLE diary_entries (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    entry_date DATE NOT NULL,
    title VARCHAR(255),
    content TEXT NOT NULL,
    mood ENUM('开心', '平静', '难过', '愤怒', '兴奋', '焦虑', '其他'),
    weather VARCHAR(50),
    location VARCHAR(255),
    tags VARCHAR(500),
    is_private BOOLEAN DEFAULT TRUE,
    is_favorite BOOLEAN DEFAULT FALSE,
    word_count INT DEFAULT 0,
    reading_time INT DEFAULT 0,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### 3. 标签表 (tags)
```sql
CREATE TABLE tags (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    tag_name VARCHAR(100) NOT NULL,
    tag_type ENUM('reading', 'diary'),
    color VARCHAR(7) DEFAULT '#1890ff',
    use_count INT DEFAULT 0,
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

## 项目结构

```
├── backend/                          # 后端代码
│   ├── src/main/java/com/permission/
│   │   ├── entity/                   # 实体类
│   │   │   ├── ReadingNote.java      # 读书笔记实体
│   │   │   ├── DiaryEntry.java       # 日记实体
│   │   │   └── Tag.java              # 标签实体
│   │   ├── mapper/                   # 数据访问层
│   │   │   ├── ReadingNoteMapper.java
│   │   │   └── DiaryEntryMapper.java
│   │   ├── service/                  # 业务逻辑层
│   │   │   ├── ReadingNoteService.java
│   │   │   ├── ReadingNoteServiceImpl.java
│   │   │   ├── DiaryEntryService.java
│   │   │   └── DiaryEntryServiceImpl.java
│   │   └── controller/               # 控制器层
│   │       ├── ReadingNoteController.java
│   │       └── DiaryEntryController.java
│   └── src/main/resources/
│       └── mapper/                   # MyBatis映射文件
│           └── ReadingNoteMapper.xml
├── frontend/                         # 前端代码
│   ├── src/views/
│   │   ├── ReadingNotes.vue          # 读书笔记页面
│   │   └── Diary.vue                 # 日记页面
│   ├── src/router/
│   │   └── index.js                  # 路由配置
│   └── src/layout/
│       └── MainLayout.vue            # 主布局
├── database/                         # 数据库脚本
│   └── reading_notes_and_diary_tables.sql
└── README.md                         # 项目说明
```

## API接口设计

### 读书笔记API

#### 基础CRUD操作
- `POST /api/reading-notes` - 创建读书笔记
- `GET /api/reading-notes/{id}` - 获取读书笔记详情
- `PUT /api/reading-notes/{id}` - 更新读书笔记
- `DELETE /api/reading-notes/{id}` - 删除读书笔记

#### 查询操作
- `GET /api/reading-notes/user/{userId}` - 获取用户读书笔记列表
- `GET /api/reading-notes/user/{userId}/status/{status}` - 按状态查询
- `GET /api/reading-notes/user/{userId}/search?keyword=xxx` - 搜索笔记
- `GET /api/reading-notes/user/{userId}/favorites` - 获取收藏笔记

#### 特殊操作
- `PUT /api/reading-notes/{id}/progress` - 更新阅读进度
- `PUT /api/reading-notes/{id}/favorite` - 切换收藏状态
- `GET /api/reading-notes/user/{userId}/statistics` - 获取阅读统计

### 日记记录API

#### 基础CRUD操作
- `POST /api/diary-entries` - 创建日记
- `GET /api/diary-entries/{id}` - 获取日记详情
- `PUT /api/diary-entries/{id}` - 更新日记
- `DELETE /api/diary-entries/{id}` - 删除日记

#### 查询操作
- `GET /api/diary-entries/user/{userId}` - 获取用户日记列表
- `GET /api/diary-entries/user/{userId}/date/{entryDate}` - 按日期查询
- `GET /api/diary-entries/user/{userId}/date-range` - 按日期范围查询
- `GET /api/diary-entries/user/{userId}/mood/{mood}` - 按心情查询
- `GET /api/diary-entries/user/{userId}/search?keyword=xxx` - 搜索日记

#### 特殊操作
- `PUT /api/diary-entries/{id}/favorite` - 切换收藏状态
- `GET /api/diary-entries/user/{userId}/statistics` - 获取日记统计

## 前端页面特性

### 读书笔记页面
- **卡片视图**: 美观的书籍卡片展示
- **表格视图**: 详细的列表信息展示
- **搜索筛选**: 多条件搜索和筛选
- **进度管理**: 可视化阅读进度条
- **统计面板**: 阅读数据统计展示

### 日记页面
- **日历视图**: 直观的日历形式展示
- **时间线视图**: 按时间顺序展示日记
- **心情标签**: 不同心情用不同颜色标识
- **字数统计**: 实时显示字数统计
- **标签管理**: 灵活的标签分类系统

## 部署说明

### 环境要求
- **Java**: JDK 8+
- **MySQL**: 8.0+
- **Node.js**: 16+
- **Maven**: 3.6+

### 后端部署
1. 创建数据库并执行SQL脚本
2. 配置数据库连接信息
3. 使用Maven构建项目
4. 启动Spring Boot应用

### 前端部署
1. 安装依赖: `npm install`
2. 开发环境: `npm run dev`
3. 生产构建: `npm run build`

## 使用说明

### 读书笔记使用流程
1. 添加新书籍信息
2. 设置阅读状态和进度
3. 记录阅读笔记和感悟
4. 更新阅读进度
5. 完成阅读后评分和总结

### 日记记录使用流程
1. 选择日期写日记
2. 记录当日心情和天气
3. 添加地点和标签信息
4. 撰写日记内容
5. 设置隐私和收藏状态

## 扩展功能建议

### 短期扩展
- 读书笔记详情章节管理
- 日记附件上传功能
- 数据导入导出功能
- 移动端适配优化

### 长期扩展
- 读书计划管理
- 阅读目标设定
- 日记模板系统
- 数据分析和可视化
- 社交分享功能

## 技术亮点

1. **响应式设计**: 支持多种设备访问
2. **组件化架构**: 可复用的Vue组件
3. **RESTful API**: 标准的REST接口设计
4. **数据验证**: 完善的输入验证机制
5. **性能优化**: 分页查询和缓存机制
6. **用户体验**: 直观的界面设计和交互

## 维护说明

### 日常维护
- 数据库性能监控
- 日志分析和错误排查
- 用户反馈收集和处理
- 功能优化和bug修复

### 版本更新
- 功能需求分析和设计
- 代码审查和测试
- 数据库迁移脚本
- 部署和回滚方案

## 联系方式

如有问题或建议，请联系开发团队或提交Issue。

---

**项目版本**: 1.0.0  
**最后更新**: 2024年1月  
**维护状态**: 活跃维护 