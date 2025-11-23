@echo off
:: 简化版启动脚本 - 最小化版本
chcp 65001 >nul
title 智能排课系统 - 简化启动

echo ========================================
echo    智能排课系统 - 简化启动
echo ========================================
echo.

:: 切换到脚本目录
cd /d "%~dp0"

:: 检查必要文件
if not exist "pom.xml" (
    echo [错误] 未找到pom.xml，请在项目根目录运行
    pause
    exit /b 1
)

echo [1/2] 启动后端服务...
echo.
echo 正在新窗口启动后端，请查看新打开的窗口...
start "后端服务" cmd /k "cd /d "%~dp0" && echo 正在启动后端... && mvn spring-boot:run"
echo.
echo 后端窗口已打开，等待30秒...
timeout /t 30 /nobreak
echo.

echo [2/2] 启动前端服务...
echo.

:: 检查前端依赖
if not exist "UI\coursearrange\node_modules" (
    echo [提示] 首次运行，正在安装依赖...
    cd /d "%~dp0UI\coursearrange"
    call npm install
    if errorlevel 1 (
        echo [错误] 依赖安装失败
        cd /d "%~dp0"
        pause
        exit /b 1
    )
    cd /d "%~dp0"
)

echo 正在新窗口启动前端，请查看新打开的窗口...
start "前端服务" cmd /k "cd /d "%~dp0UI\coursearrange" && echo 正在启动前端... && npm run dev"
echo.
echo 前端窗口已打开，等待15秒...
timeout /t 15 /nobreak
echo.

echo ========================================
echo    启动完成
echo ========================================
echo.
echo 服务地址:
echo   前端: http://localhost:8081
echo   后端: http://localhost:8080
echo.
echo [提示] 如果服务未启动，请查看新打开的窗口中的错误信息
echo [提示] 关闭此窗口不会停止服务
echo.

:: 尝试打开浏览器
timeout /t 3 /nobreak >nul
start http://localhost:8081

pause
