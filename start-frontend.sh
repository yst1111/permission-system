#!/bin/bash

echo "========================================"
echo "启动权限管理系统前端服务"
echo "========================================"
echo

echo "切换到前端目录..."
cd "$(dirname "$0")/frontend"

echo "检查依赖是否安装..."
if [ ! -d "node_modules" ]; then
    echo "安装依赖..."
    npm install
    if [ $? -ne 0 ]; then
        echo "依赖安装失败！"
        exit 1
    fi
fi

echo
echo "启动前端开发服务器..."
echo "前端地址: http://localhost:3000"
echo "API代理: /api -> http://localhost:8080"
echo
echo "按 Ctrl+C 停止服务"
echo

npm run dev 