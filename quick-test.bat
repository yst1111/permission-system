@echo off
chcp 65001 > nul
echo ========================================
echo 🧪 快速测试系统功能
echo ========================================
echo.

echo 🔍 检查服务状态...
echo.

echo 1. 检查后端服务...
curl -s http://localhost:8080/actuator/health >nul 2>nul
if %errorlevel% equ 0 (
    echo ✅ 后端服务运行正常
) else (
    echo ❌ 后端服务未运行或无法访问
    echo 请先运行 start-system.bat 启动系统
    pause
    exit /b 1
)

echo.
echo 2. 检查前端服务...
curl -s http://localhost:3000 >nul 2>nul
if %errorlevel% equ 0 (
    echo ✅ 前端服务运行正常
) else (
    echo ❌ 前端服务未运行或无法访问
    echo 请先运行 start-system.bat 启动系统
    pause
    exit /b 1
)

echo.
echo 3. 测试API接口...
echo.

echo 📝 测试获取用户日记...
curl -s -X GET "http://localhost:8080/api/diary-entries/user/1" -H "Content-Type: application/json"
if %errorlevel% equ 0 (
    echo ✅ API接口测试通过
) else (
    echo ❌ API接口测试失败
)

echo.
echo 4. 测试数据库连接...
echo 请检查后端控制台是否有数据库连接错误
echo.

echo ========================================
echo 🎯 测试完成！
echo ========================================
echo.
echo 📋 下一步操作:
echo   1. 访问 http://localhost:3000 使用前端界面
echo   2. 访问 http://localhost:8080/swagger-ui.html 查看API文档
echo   3. 尝试创建、编辑、删除日记
echo   4. 测试搜索和筛选功能
echo.
echo 🔧 如果遇到问题:
echo   - 检查浏览器控制台错误信息
echo   - 检查后端控制台日志
echo   - 确认数据库连接正常
echo.
pause 