@echo off
echo ========================================
echo 初始化数据库
echo ========================================
echo.

echo 请确保MySQL服务已启动，并且可以连接到数据库
echo.

set /p DB_HOST=请输入数据库主机地址 (默认: localhost): 
if "%DB_HOST%"=="" set DB_HOST=localhost

set /p DB_PORT=请输入数据库端口 (默认: 3306): 
if "%DB_PORT%"=="" set DB_PORT=3306

set /p DB_USER=请输入数据库用户名 (默认: root): 
if "%DB_USER%"=="" set DB_USER=root

set /p DB_PASS=请输入数据库密码: 

echo.
echo 正在创建数据库和表...
echo.

mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASS% -e "CREATE DATABASE IF NOT EXISTS permission_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

mysql -h%DB_HOST% -P%DB_PORT% -u%DB_USER% -p%DB_PASS% permission_system < backend\src\main\resources\db\init-diary.sql

echo.
echo 数据库初始化完成！
echo.
echo 现在可以启动系统了：
echo 1. 运行 start-system.bat 启动系统
echo 2. 或者手动启动前后端服务
echo.
pause 