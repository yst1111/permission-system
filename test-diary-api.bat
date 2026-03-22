@echo off
echo ========================================
echo 测试日记API接口
echo ========================================
echo.

echo 请确保后端服务已启动 (http://localhost:8080)
echo.

set /p BASE_URL=请输入后端服务地址 (默认: http://localhost:8080): 
if "%BASE_URL%"=="" set BASE_URL=http://localhost:8080

echo.
echo 开始测试API接口...
echo.

echo 1. 测试获取用户日记列表...
curl -X GET "%BASE_URL%/api/diary-entries/user/1" -H "Content-Type: application/json"
echo.
echo.

echo 2. 测试创建日记...
curl -X POST "%BASE_URL%/api/diary-entries" -H "Content-Type: application/json" -d "{\"userId\":1,\"entryDate\":\"2024-01-15\",\"title\":\"测试日记\",\"content\":\"这是一篇测试日记内容\",\"mood\":\"开心\",\"weather\":\"晴天\",\"location\":\"家里\",\"tags\":\"测试,日记\",\"isPrivate\":false,\"isFavorite\":false}"
echo.
echo.

echo 3. 测试获取日记统计...
curl -X GET "%BASE_URL%/api/diary-entries/user/1/statistics?year=2024&month=1" -H "Content-Type: application/json"
echo.
echo.

echo 4. 测试搜索日记...
curl -X GET "%BASE_URL%/api/diary-entries/user/1/search?keyword=测试" -H "Content-Type: application/json"
echo.
echo.

echo API测试完成！
echo.
pause 