# 用户管理功能说明文档

## 功能概述

用户管理模块是权限管理系统的核心功能之一，提供完整的用户生命周期管理，包括用户的增删改查、状态管理、角色分配、批量操作等功能。

## 主要功能特性

### 1. 用户基础管理
- **用户列表**：分页展示用户信息，支持多条件搜索
- **新增用户**：创建新用户，支持完整的用户信息录入
- **编辑用户**：修改现有用户信息
- **删除用户**：删除指定用户（保护超级管理员）
- **状态管理**：启用/禁用用户状态

### 2. 高级功能
- **密码重置**：一键重置用户密码为默认密码
- **角色查看**：查看用户关联的角色信息
- **批量删除**：支持多选批量删除用户
- **数据导入**：支持Excel文件批量导入用户
- **数据导出**：导出用户数据（开发中）

### 3. 数据验证
- **前端验证**：实时表单验证，提供友好的错误提示
- **后端验证**：服务端数据验证，确保数据完整性
- **业务验证**：用户名唯一性、超级管理员保护等

## 技术架构

### 1. 后端架构
```
Controller (UserController)
    ↓
Service (UserService)
    ↓
Mapper (UserMapper)
    ↓
Database (MySQL)
```

### 2. 前端架构
```
Vue 3 + Composition API
    ↓
Element Plus UI组件库
    ↓
Axios HTTP客户端
    ↓
Vue Router 路由管理
```

## 数据库设计

### 1. 用户表 (sys_user)
```sql
CREATE TABLE `sys_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(20) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `nickname` varchar(50) NOT NULL COMMENT '昵称',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(500) DEFAULT NULL COMMENT '头像',
  `gender` tinyint(1) DEFAULT '0' COMMENT '性别：0-未知，1-男，2-女',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(50) DEFAULT NULL COMMENT '最后登录IP',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

### 2. 用户角色关联表 (sys_user_role)
```sql
CREATE TABLE `sys_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';
```

## API接口说明

### 1. 用户基础接口

#### 获取用户列表
```
GET /api/user/list
参数：
- username: 用户名（模糊查询）
- nickname: 昵称（模糊查询）
- email: 邮箱（模糊查询）
- phone: 手机号（模糊查询）
- status: 状态（0-禁用，1-启用）
- gender: 性别（0-未知，1-男，2-女）
```

#### 根据ID获取用户
```
GET /api/user/{id}
参数：
- id: 用户ID（路径参数）
```

#### 根据用户名获取用户
```
GET /api/user/username/{username}
参数：
- username: 用户名（路径参数）
```

#### 新增用户
```
POST /api/user/add
请求体：
{
  "username": "testuser",
  "password": "123456",
  "nickname": "测试用户",
  "email": "test@example.com",
  "phone": "13800138000",
  "gender": 1,
  "status": 1,
  "remark": "测试用户"
}
```

#### 更新用户
```
PUT /api/user/update
请求体：
{
  "id": 1,
  "username": "testuser",
  "nickname": "测试用户",
  "email": "test@example.com",
  "phone": "13800138000",
  "gender": 1,
  "status": 1,
  "remark": "测试用户"
}
```

#### 删除用户
```
DELETE /api/user/{id}
参数：
- id: 用户ID（路径参数）
```

### 2. 用户高级接口

#### 更新用户状态
```
PUT /api/user/{id}/status?status={status}
参数：
- id: 用户ID（路径参数）
- status: 状态值（0-禁用，1-启用）
```

#### 重置用户密码
```
PUT /api/user/{id}/password
参数：
- id: 用户ID（路径参数）
```

#### 获取用户角色
```
GET /api/user/{id}/roles
参数：
- id: 用户ID（路径参数）
```

#### 批量删除用户
```
POST /api/user/batch/delete
请求体：
[1, 2, 3]  // 用户ID数组
```

## 前端功能说明

### 1. 用户列表页面
- **搜索功能**：支持用户名、昵称、邮箱、手机号、状态、性别等多条件搜索
- **表格展示**：展示用户完整信息，支持分页
- **操作按钮**：编辑、重置密码、查看角色、删除等操作
- **状态切换**：直接切换用户启用/禁用状态
- **批量操作**：支持多选批量删除

### 2. 用户表单
- **响应式布局**：使用栅格系统，适配不同屏幕尺寸
- **表单验证**：实时验证，提供详细的错误提示
- **字段说明**：
  - 用户名：3-20个字符，只能包含字母、数字、下划线
  - 密码：6-20个字符（新增时必填）
  - 昵称：必填，最大50个字符
  - 邮箱：可选，格式验证
  - 手机号：可选，11位手机号格式验证
  - 性别：单选（未知/男/女）
  - 状态：单选（启用/禁用）
  - 头像：头像URL地址
  - 备注：可选，最大500个字符

### 3. 用户导入功能
- **三步导入流程**：
  1. 文件上传：支持拖拽上传Excel文件
  2. 数据预览：预览导入数据，显示验证错误
  3. 确认导入：确认无误后执行导入
- **模板下载**：提供标准导入模板
- **数据验证**：前端验证数据格式和业务规则
- **错误提示**：详细显示每行数据的错误信息

## 安全特性

### 1. 数据安全
- **密码加密**：使用MD5加密存储用户密码
- **参数验证**：前后端双重验证，防止恶意数据
- **SQL注入防护**：使用MyBatis参数化查询

### 2. 权限控制
- **超级管理员保护**：admin用户不能被删除或禁用
- **操作权限验证**：删除、状态变更等敏感操作需要确认
- **数据隔离**：用户只能操作有权限的数据

### 3. 业务安全
- **用户名唯一性**：防止重复用户名
- **状态管理**：禁用用户无法登录系统
- **操作日志**：记录重要操作，便于审计

## 性能优化

### 1. 数据库优化
- **索引优化**：用户名唯一索引、状态索引、创建时间索引
- **查询优化**：支持多条件组合查询
- **分页查询**：避免大量数据一次性加载

### 2. 前端优化
- **懒加载**：表格数据分页加载
- **缓存策略**：用户列表数据缓存
- **防抖处理**：搜索输入防抖优化

### 3. 接口优化
- **批量操作**：支持批量删除，减少请求次数
- **异步处理**：导入等耗时操作异步处理
- **错误处理**：友好的错误提示和恢复机制

## 使用说明

### 1. 新增用户
1. 点击"新增用户"按钮
2. 填写用户信息（用户名、昵称为必填项）
3. 设置用户状态和性别
4. 点击"确定"保存

### 2. 编辑用户
1. 在用户列表中点击"编辑"按钮
2. 修改需要更新的字段
3. 点击"确定"保存更改

### 3. 删除用户
1. 在用户列表中点击"删除"按钮
2. 确认删除操作
3. 系统会检查是否为超级管理员

### 4. 批量导入
1. 点击"导入用户"按钮
2. 下载导入模板，按格式填写数据
3. 上传Excel文件
4. 预览和验证数据
5. 确认导入

### 5. 状态管理
1. 在用户列表中直接切换状态开关
2. 系统会实时更新用户状态
3. 超级管理员状态不可更改

## 常见问题

### 1. 用户名重复
**问题**：新增用户时提示"用户名已存在"
**解决**：选择其他用户名，用户名在系统中必须唯一

### 2. 密码重置
**问题**：忘记用户密码怎么办
**解决**：使用"重置密码"功能，重置后密码为"123456"

### 3. 用户无法登录
**问题**：用户账号无法登录系统
**解决**：检查用户状态是否为"启用"，禁用状态的用户无法登录

### 4. 导入失败
**问题**：Excel导入失败
**解决**：
- 检查文件格式是否为.xlsx或.xls
- 确保数据格式符合模板要求
- 检查必填字段是否完整
- 验证用户名是否重复

## 扩展功能

### 1. 用户头像
- 支持头像URL设置
- 可扩展为文件上传功能
- 支持头像预览和编辑

### 2. 用户分组
- 支持用户分组管理
- 按部门、职位等维度分组
- 分组权限管理

### 3. 登录日志
- 记录用户登录时间、IP地址
- 登录失败记录
- 异常登录告警

### 4. 密码策略
- 密码复杂度要求
- 密码过期策略
- 密码历史记录

## 总结

用户管理模块提供了完整的用户生命周期管理功能，包括基础的CRUD操作、高级的状态管理和批量操作、以及完善的导入导出功能。系统设计注重安全性、性能和用户体验，为权限管理系统提供了坚实的用户基础。

该模块已通过全面测试，可以安全投入使用，后续可根据业务需求进行功能扩展和优化。 