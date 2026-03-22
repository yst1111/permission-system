# 编程规范与习惯

## 核心理念
- **不修改现有功能** - 只新增不改动，避免破坏已有功能
- **实时推送 Git** - 每次修改后立即 commit + push

## 后端规范 (Spring Boot)

### 代码结构
- Controller → Service → Mapper 三层分离
- 每个功能模块单独一个包

### 命名规范
- 类名：大驼峰 (UserService, JobMapper)
- 方法名：小驼峰 (getUserList, insertJob)
- 数据库表/字段：下划线 (user_name, create_time)

### API 设计
- GET 获取 → /api/xxx/list
- POST 新增 → /api/xxx
- PUT 更新 → /api/xxx
- DELETE 删除 → /api/xxx/{id}

## 前端规范 (Vue3 + Element Plus)

### 组件规范
- 使用 Composition API (<script setup>)
- 用 Vue Router 做路由
- 用 Element Plus 组件库

### 命名规范
- 文件名：大驼峰 (UserList.vue, JobManage.vue)
- 组件内变量：小驼峰

## 数据库规范

### MySQL
- 字符集：utf8mb4
- 主键：id (自增)
- 时间字段：create_time, update_time
- 软删除：用 status 字段，不用 delete

## 定时任务规范

### Quartz 集成
- Handler 放在 `job/handler/` 目录
- 用 @Component 注解
- 启动时自动注册到数据库

### Cron 表达式格式
- `0/5 * * * * ?` - 每5秒
- `0 0 * * * ?` - 每小时
- `0 0 0 * * ?` - 每天凌晨
- `0 0 2 * * ?` - 每天2点

## 菜单权限

### 角色管理
- 超级管理员 (SUPER_ADMIN) - 全部菜单
- 一般用户 (GENERAL_USER) - 受限菜单

### 实现方式
- 数据库 sys_role 表存角色
- 数据库 sys_menu 表存菜单
- 数据库 sys_role_menu 关联角色和菜单

## 项目配置

### 端口
- 后端：8080
- 前端：3000
- 数据库：3306

### 数据库连接
- Windows IP: 172.22.128.1
- 不能用 localhost
