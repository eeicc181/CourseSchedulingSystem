#!/bin/bash

# 设置颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo "========================================"
echo "   智能排课管理系统 - 一键启动脚本"
echo "========================================"
echo ""

# 检查Java环境
echo -e "${BLUE}[1/4] 检查Java环境...${NC}"
if ! command -v java &> /dev/null; then
    echo -e "${RED}[错误] 未检测到Java环境，请先安装JDK 1.8或以上版本${NC}"
    exit 1
fi
echo -e "${GREEN}[√] Java环境检测通过${NC}"
java -version
echo ""

# 检查Node.js环境
echo -e "${BLUE}[2/4] 检查Node.js环境...${NC}"
if ! command -v node &> /dev/null; then
    echo -e "${RED}[错误] 未检测到Node.js环境，请先安装Node.js 6.0或以上版本${NC}"
    exit 1
fi
echo -e "${GREEN}[√] Node.js环境检测通过${NC}"
node -v
echo ""

# 检查npm环境
if ! command -v npm &> /dev/null; then
    echo -e "${RED}[错误] 未检测到npm，请确保Node.js安装正确${NC}"
    exit 1
fi
echo -e "${GREEN}[√] npm环境检测通过${NC}"
npm -v
echo ""

# 获取脚本所在目录
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

# 启动后端服务
echo -e "${BLUE}[3/4] 启动后端服务...${NC}"
echo "正在启动Spring Boot应用..."

# 后台启动后端
nohup mvn spring-boot:run > logs/backend.log 2>&1 &
BACKEND_PID=$!
echo $BACKEND_PID > logs/backend.pid
echo -e "${GREEN}[√] 后端服务启动中... (PID: $BACKEND_PID, 端口: 8080)${NC}"
echo ""

# 等待后端启动
echo "等待后端服务启动 (预计30秒)..."
sleep 30
echo ""

# 启动前端服务
echo -e "${BLUE}[4/4] 启动前端服务...${NC}"
echo "正在启动Vue开发服务器..."

# 检查是否已安装依赖
if [ ! -d "UI/coursearrange/node_modules" ]; then
    echo -e "${YELLOW}[提示] 首次运行，正在安装前端依赖...${NC}"
    cd UI/coursearrange
    npm install
    cd ../..
fi

# 后台启动前端
cd UI/coursearrange
nohup npm run dev > ../../logs/frontend.log 2>&1 &
FRONTEND_PID=$!
echo $FRONTEND_PID > ../../logs/frontend.pid
cd ../..

echo -e "${GREEN}[√] 前端服务启动中... (PID: $FRONTEND_PID, 端口: 8081)${NC}"
echo ""

# 等待前端启动
echo "等待前端服务启动 (预计10秒)..."
sleep 10
echo ""

echo "========================================"
echo "   启动完成！"
echo "========================================"
echo ""
echo "服务信息:"
echo "  后端服务: http://localhost:8080"
echo "  前端服务: http://localhost:8081"
echo "  API文档:  http://localhost:8080/swagger-ui.html"
echo ""
echo "默认账号:"
echo "  管理员: admin / 123456"
echo "  教师:   teacher01 / 123456"
echo "  学生:   2020011234 / 123456"
echo ""
echo "进程信息:"
echo "  后端PID: $BACKEND_PID (日志: logs/backend.log)"
echo "  前端PID: $FRONTEND_PID (日志: logs/frontend.log)"
echo ""
echo -e "${YELLOW}[提示] 使用 ./stop.sh 停止所有服务${NC}"
echo ""

# 尝试打开浏览器
if command -v xdg-open &> /dev/null; then
    xdg-open http://localhost:8081
elif command -v open &> /dev/null; then
    open http://localhost:8081
fi
