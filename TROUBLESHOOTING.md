# 🔧 日记系统问题排查指南

## 🚨 常见问题及解决方案

### 1. 前端无法加载日记数据

#### 症状
- 页面显示"加载日记失败"
- 控制台显示API请求错误
- 日记列表为空

#### 可能原因
1. **后端服务未启动**
2. **数据库连接失败**
3. **API路径错误**
4. **CORS跨域问题**

#### 解决方案
```bash
# 1. 检查后端服务状态
curl http://localhost:8080/actuator/health

# 2. 检查数据库连接
# 查看后端控制台日志

# 3. 测试API接口
curl http://localhost:8080/api/diary-entries/user/1

# 4. 重启系统
start-system.bat
```

### 2. 创建/编辑日记失败

#### 症状
- 点击保存按钮无反应
- 显示"保存失败"错误
- 表单验证不通过

#### 可能原因
1. **表单验证失败**
2. **后端数据格式不匹配**
3. **数据库字段约束**
4. **用户ID未设置**

#### 解决方案
```bash
# 1. 检查浏览器控制台错误
# 2. 检查后端日志
# 3. 验证表单数据格式
# 4. 确认用户ID设置
```

### 3. 搜索和筛选不工作

#### 症状
- 搜索框输入无效果
- 筛选条件不生效
- 日期范围选择异常

#### 可能原因
1. **前端计算属性错误**
2. **数据格式不一致**
3. **筛选逻辑bug**

#### 解决方案
```bash
# 1. 检查浏览器控制台
# 2. 验证数据格式
# 3. 测试筛选逻辑
```

### 4. 导入导出功能异常

#### 症状
- 导出文件为空或损坏
- 导入数据失败
- 文件格式错误

#### 可能原因
1. **文件权限问题**
2. **数据序列化错误**
3. **用户ID不匹配**

#### 解决方案
```bash
# 1. 检查文件权限
# 2. 验证JSON格式
# 3. 确认用户ID
```

## 🔍 调试步骤

### 1. 检查服务状态
```bash
# 后端服务
netstat -an | findstr :8080

# 前端服务
netstat -an | findstr :3000

# 数据库服务
netstat -an | findstr :3306
```

### 2. 查看日志
```bash
# 后端日志
# 查看Spring Boot控制台输出

# 前端日志
# 查看浏览器开发者工具Console
```

### 3. 测试API接口
```bash
# 使用curl测试
curl -X GET "http://localhost:8080/api/diary-entries/user/1"

# 使用Postman测试
# 导入API文档中的接口
```

### 4. 检查数据库
```sql
-- 连接数据库
mysql -u root -p permission_system

-- 检查表结构
DESCRIBE diary_entries;

-- 检查数据
SELECT * FROM diary_entries LIMIT 5;
```

## 🛠️ 修复工具

### 1. 重置系统
```bash
# 停止所有服务
taskkill /f /im java.exe
taskkill /f /im node.exe

# 清理缓存
cd frontend
npm run clean

cd ../backend
mvn clean

# 重新启动
start-system.bat
```

### 2. 重建数据库
```bash
# 删除并重建数据库
mysql -u root -p -e "DROP DATABASE IF EXISTS permission_system; CREATE DATABASE permission_system;"

# 重新初始化
init-database.bat
```

### 3. 更新依赖
```bash
# 前端依赖
cd frontend
npm update

# 后端依赖
cd ../backend
mvn dependency:resolve
```

## 📞 获取帮助

### 1. 检查文档
- 查看 `README.md`
- 查看 `API文档` (http://localhost:8080/swagger-ui.html)

### 2. 查看日志
- 后端控制台日志
- 浏览器开发者工具
- 数据库错误日志

### 3. 常见错误码
- `401`: 未授权，检查用户登录状态
- `403`: 禁止访问，检查权限配置
- `404`: 资源不存在，检查API路径
- `500`: 服务器错误，查看后端日志

### 4. 联系支持
如果问题仍然存在，请提供以下信息：
- 错误截图
- 控制台日志
- 复现步骤
- 系统环境信息

---

💡 **提示**: 大多数问题都可以通过重启系统解决。如果问题持续存在，请按照上述步骤逐一排查。 