@echo off
chcp 65001 > nul
echo ========================================
echo 🚀 启动读书笔记和日记系统
echo ========================================
echo.

echo 📋 检查环境...
where java >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ 错误: 未找到Java环境，请先安装Java 8+
    pause
    exit /b 1
)

where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ 错误: 未找到Maven，请先安装Maven 3.6+
    pause
    exit /b 1
)

where node >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ 错误: 未找到Node.js，请先安装Node.js 14+
    pause
    exit /b 1
)

echo ✅ 环境检查通过
echo.

echo 🔍 检查数据库连接...
echo 请确保MySQL服务已启动 (端口: 3306)
echo 数据库 'permission_system' 已创建
echo.

echo 1. 🚀 启动后端服务...
cd backend
start "Backend Server" cmd /k "echo 正在启动后端服务... && mvn spring-boot:run"
echo 后端服务启动中，请等待...
timeout /t 15 /nobreak > nul

echo.
echo 2. 🌐 启动前端服务...
cd ..\frontend
start "Frontend Server" cmd /k "echo 正在启动前端服务... && npm run dev"
echo 前端服务启动中，请等待...
timeout /t 8 /nobreak > nul

echo.
echo ========================================
echo 🎉 系统启动完成！
echo ========================================
echo.
echo 📍 服务地址:
echo   后端服务: http://localhost:8080
echo   前端服务: http://localhost:3000 (或 3001)
echo   API文档: http://localhost:8080/swagger-ui.html
echo.
echo 📋 使用说明:
echo   1. 等待后端服务完全启动 (约30秒)
echo   2. 访问前端地址开始使用
echo   3. 如果遇到问题，请检查控制台错误信息
echo.
echo 🔧 故障排除:
echo   - 端口被占用: 修改配置文件中的端口号
echo   - 数据库连接失败: 检查MySQL服务和配置
echo   - 前端无法访问: 检查Node.js和npm版本
echo.
echo 按任意键打开浏览器...
pause >nul
start http://localhost:3000
start http://localhost:8080/swagger-ui.html 