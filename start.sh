#!/bin/bash

echo "========================================"
echo "权限管理系统启动脚本"
echo "========================================"
echo

echo "正在启动后端服务..."
cd backend
gnome-terminal --title="后端服务" -- bash -c "mvn spring-boot:run; exec bash" &
echo "后端服务启动中，请等待..."
sleep 10

echo
echo "正在启动前端服务..."
cd ../frontend
gnome-terminal --title="前端服务" -- bash -c "npm run dev; exec bash" &
echo "前端服务启动中，请等待..."
sleep 5

echo
echo "========================================"
echo "服务启动完成！"
echo "========================================"
echo "后端服务: http://localhost:8080"
echo "前端服务: http://localhost:3000"
echo
echo "请确保："
echo "1. MySQL服务已启动"
echo "2. 数据库已创建并执行init.sql"
echo "3. Java 8环境已配置"
echo "4. Node.js环境已配置"
echo
echo "按回车键退出..."
read 