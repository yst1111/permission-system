# 📚 读书笔记和日记系统

一个功能完整的个人知识管理系统，支持读书笔记记录和日记写作。

## ✨ 主要功能

### 📖 读书笔记模块
- 📚 书籍信息管理（标题、作者、ISBN等）
- 📖 阅读进度跟踪
- ✍️ 笔记内容记录
- 🏷️ 标签分类管理
- ⭐ 收藏和评分功能
- 📊 阅读统计和分析

### 📝 日记模块
- 📅 日历视图和列表视图
- 😊 心情状态记录
- 🌤️ 天气和地点记录
- 🏷️ 智能标签管理
- 🔒 私密日记保护
- 📊 统计数据分析
- 📤 导入导出功能

## 🚀 快速开始

### 环境要求
- Java 8+
- Node.js 14+
- MySQL 5.7+
- Maven 3.6+

### 1. 克隆项目
```bash
git clone <repository-url>
cd coding
```

### 2. 初始化数据库
```bash
# Windows
init-database.bat

# 或者手动执行SQL脚本
mysql -u root -p < backend/src/main/resources/db/init-diary.sql
```

### 3. 启动系统
```bash
# Windows (推荐)
start-system.bat

# 或者手动启动
# 后端
cd backend
mvn spring-boot:run

# 前端
cd frontend
npm run dev
```

### 4. 访问系统
- 前端界面: http://localhost:3000
- 后端API: http://localhost:8080
- API文档: http://localhost:8080/swagger-ui.html

## 🏗️ 系统架构

### 后端技术栈
- **框架**: Spring Boot 2.7+
- **数据库**: MySQL + MyBatis
- **安全**: Spring Security + JWT
- **文档**: Swagger/OpenAPI 3
- **构建**: Maven

### 前端技术栈
- **框架**: Vue 3 + Element Plus
- **构建**: Vite
- **路由**: Vue Router
- **状态管理**: Pinia
- **HTTP客户端**: Axios

### 数据库设计
```
├── users (用户表)
├── diary_entries (日记记录表)
├── reading_notes (读书笔记表)
├── tags (标签表)
└── diary_attachments (日记附件表)
```

## 📱 功能特性

### 🔍 智能搜索
- 全文搜索（标题、内容、标签）
- 多条件筛选（日期、心情、标签等）
- 实时搜索建议

### 📊 数据统计
- 阅读时长统计
- 日记数量统计
- 心情变化趋势
- 标签使用频率

### 🎨 用户体验
- 响应式设计
- 深色/浅色主题
- 快捷键支持
- 数据导入导出

## 🔧 开发指南

### 项目结构
```
├── backend/                 # 后端代码
│   ├── src/main/java/
│   │   ├── controller/     # 控制器层
│   │   ├── service/        # 业务逻辑层
│   │   ├── mapper/         # 数据访问层
│   │   └── entity/         # 实体类
│   └── src/main/resources/
│       ├── mapper/         # MyBatis映射文件
│       └── db/            # 数据库脚本
├── frontend/               # 前端代码
│   ├── src/
│   │   ├── views/         # 页面组件
│   │   ├── components/    # 通用组件
│   │   ├── utils/         # 工具函数
│   │   └── router/        # 路由配置
│   └── public/            # 静态资源
└── docs/                  # 项目文档
```

### API接口
所有API接口都遵循RESTful设计原则，支持：
- 标准的HTTP方法（GET、POST、PUT、DELETE）
- 统一的响应格式
- 完整的错误处理
- JWT身份认证

### 数据库操作
- 使用MyBatis进行数据访问
- 支持动态SQL查询
- 完整的CRUD操作
- 事务管理

## 🚀 部署说明

### 开发环境
```bash
# 后端
mvn spring-boot:run -Dspring.profiles.active=dev

# 前端
npm run dev
```

### 生产环境
```bash
# 后端打包
mvn clean package -Dmaven.test.skip=true

# 前端构建
npm run build

# 部署
java -jar backend/target/permission-system-1.0.0.jar
```

## 📝 更新日志

### v1.0.0 (2024-01-15)
- ✨ 初始版本发布
- 📚 读书笔记基础功能
- 📝 日记记录基础功能
- 🏷️ 标签管理系统
- 📊 基础统计功能

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 📞 联系方式

- 项目主页: [GitHub Repository]
- 问题反馈: [Issues]
- 功能建议: [Discussions]

---

⭐ 如果这个项目对你有帮助，请给它一个星标！ 