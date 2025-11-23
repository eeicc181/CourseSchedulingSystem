@echo off
chcp 65001 >nul
title 智能排课系统 - 停止服务

echo ========================================
echo    智能排课管理系统 - 停止服务脚本
echo ========================================
echo.

echo [提示] 正在停止所有服务...
echo.

:: 停止占用8080端口的进程（后端）
echo [1/2] 停止后端服务 (端口: 8080)...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8080 ^| findstr LISTENING') do (
    taskkill /F /PID %%a >nul 2>&1
    if %errorlevel% equ 0 (
        echo [√] 后端服务已停止 (PID: %%a)
    )
)
echo.

:: 停止占用8081端口的进程（前端）
echo [2/2] 停止前端服务 (端口: 8081)...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8081 ^| findstr LISTENING') do (
    taskkill /F /PID %%a >nul 2>&1
    if %errorlevel% equ 0 (
        echo [√] 前端服务已停止 (PID: %%a)
    )
)
echo.

:: 停止所有Java进程（可选，谨慎使用）
:: taskkill /F /IM java.exe >nul 2>&1

:: 停止所有Node进程（可选，谨慎使用）
:: taskkill /F /IM node.exe >nul 2>&1

echo ========================================
echo    所有服务已停止！
echo ========================================
echo.
pause
