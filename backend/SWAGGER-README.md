# Swagger API文档配置说明

## 📋 配置概述
本项目使用 **Spring Boot 2.7.18** + **Swagger 2.9.2** 来生成API文档。

## 🔧 核心配置

### 1. Maven依赖
```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.9.2</version>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>2.9.2</version>
</dependency>
```

### 2. 配置类
- **SwaggerConfig.java**: Swagger核心配置
- **WebConfig.java**: Web资源配置，确保Swagger UI正常访问

### 3. 应用配置
```yaml
spring:
  mvc:
    pathmatch:
      matching-strategy: ant_path_matcher
```

## 🌐 访问地址

### 主要地址
- **Swagger UI界面**: http://localhost:8080/swagger-ui.html
- **API文档JSON**: http://localhost:8080/v2/api-docs

### 备用地址
- **Swagger UI**: http://localhost:8080/swagger-ui/
- **API文档**: http://localhost:8080/api-docs

## 🚀 启动步骤

1. **启动应用**: 运行 `PermissionApplication.java`
2. **等待启动**: 看到"权限管理系统启动成功！"消息
3. **访问Swagger**: 浏览器打开 http://localhost:8080/swagger-ui.html

## 🔍 故障排除

### 问题1: 访问 /v2/api-docs 没有内容
**原因**: 可能是控制器没有被正确扫描
**解决**: 
- 检查控制器是否有 `@RestController` 注解
- 检查控制器是否在 `com.permission.controller` 包下
- 检查是否有 `@Api` 注解

### 问题2: Swagger UI页面无法访问
**原因**: 资源路径配置问题
**解决**: 
- 检查 `WebConfig.java` 中的资源处理器配置
- 确保 `swagger-ui.html` 资源路径正确

### 问题3: 启动时出现Swagger相关错误
**原因**: 版本兼容性问题
**解决**: 
- 确保使用 Swagger 2.9.2 版本
- 确保 Spring Boot 版本为 2.7.x
- 检查 `application.yml` 中的 `matching-strategy` 配置

## 📝 控制器注解示例

```java
@Api(tags = "用户管理")
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @ApiOperation("获取用户信息")
    @GetMapping("/{id}")
    public Result<User> getUser(@PathVariable Long id) {
        // 实现代码
    }
}
```

## 🧪 测试命令

### 使用curl测试
```bash
# 测试健康检查
curl http://localhost:8080/api/test/health

# 测试Swagger文档
curl http://localhost:8080/v2/api-docs

# 测试Swagger UI页面
curl -I http://localhost:8080/swagger-ui.html
```

### 使用浏览器测试
1. 直接访问: http://localhost:8080/swagger-ui.html
2. 查看网络请求，确认资源加载正常
3. 检查浏览器控制台是否有错误信息

## ✅ 成功标志

当配置正确时，您应该能看到：
- Swagger UI页面正常显示
- API接口列表完整
- 可以展开查看接口详情
- 可以测试接口调用 