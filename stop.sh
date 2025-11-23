#!/bin/bash

# 设置颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo "========================================"
echo "   智能排课管理系统 - 停止服务脚本"
echo "========================================"
echo ""

# 获取脚本所在目录
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

# 停止后端服务
echo -e "${BLUE}[1/2] 停止后端服务...${NC}"
if [ -f "logs/backend.pid" ]; then
    BACKEND_PID=$(cat logs/backend.pid)
    if ps -p $BACKEND_PID > /dev/null 2>&1; then
        kill $BACKEND_PID
        echo -e "${GREEN}[√] 后端服务已停止 (PID: $BACKEND_PID)${NC}"
        rm logs/backend.pid
    else
        echo -e "${YELLOW}[提示] 后端服务未运行${NC}"
        rm logs/backend.pid
    fi
else
    # 尝试通过端口查找并停止
    BACKEND_PID=$(lsof -ti:8080)
    if [ ! -z "$BACKEND_PID" ]; then
        kill $BACKEND_PID
        echo -e "${GREEN}[√] 后端服务已停止 (PID: $BACKEND_PID)${NC}"
    else
        echo -e "${YELLOW}[提示] 后端服务未运行${NC}"
    fi
fi
echo ""

# 停止前端服务
echo -e "${BLUE}[2/2] 停止前端服务...${NC}"
if [ -f "logs/frontend.pid" ]; then
    FRONTEND_PID=$(cat logs/frontend.pid)
    if ps -p $FRONTEND_PID > /dev/null 2>&1; then
        kill $FRONTEND_PID
        echo -e "${GREEN}[√] 前端服务已停止 (PID: $FRONTEND_PID)${NC}"
        rm logs/frontend.pid
    else
        echo -e "${YELLOW}[提示] 前端服务未运行${NC}"
        rm logs/frontend.pid
    fi
else
    # 尝试通过端口查找并停止
    FRONTEND_PID=$(lsof -ti:8081)
    if [ ! -z "$FRONTEND_PID" ]; then
        kill $FRONTEND_PID
        echo -e "${GREEN}[√] 前端服务已停止 (PID: $FRONTEND_PID)${NC}"
    else
        echo -e "${YELLOW}[提示] 前端服务未运行${NC}"
    fi
fi
echo ""

# 清理可能残留的进程
echo -e "${BLUE}清理残留进程...${NC}"
pkill -f "spring-boot:run" 2>/dev/null
pkill -f "webpack-dev-server" 2>/dev/null
echo -e "${GREEN}[√] 清理完成${NC}"
echo ""

echo "========================================"
echo "   所有服务已停止！"
echo "========================================"
echo ""
