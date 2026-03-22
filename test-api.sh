#!/bin/bash

echo "========================================"
echo "权限管理系统API测试脚本"
echo "========================================"
echo

echo "正在测试后端服务..."
echo

echo "1. 测试健康检查接口..."
curl -s http://localhost:8080/api/test/health
echo
echo

echo "2. 测试地区列表接口..."
curl -s "http://localhost:8080/api/region/list?regionName=北京"
echo
echo

echo "3. 测试地区树形接口..."
curl -s http://localhost:8080/api/region/tree
echo
echo

echo "========================================"
echo "API测试完成！"
echo "========================================"
echo
echo "如果看到JSON响应，说明API正常工作"
echo "如果看到连接错误，请检查后端服务是否启动"
echo
echo "按回车键退出..."
read 