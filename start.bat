@echo off
chcp 65001 >nul 2>nul
title 智能排课系统 - 一键启动

:: 设置颜色（如果支持）
color 0A

echo ========================================
echo    智能排课管理系统 - 一键启动脚本
echo ========================================
echo.
echo 当前目录: %CD%
echo 脚本目录: %~dp0
echo.

:: 切换到脚本所在目录
cd /d "%~dp0"
echo 已切换到项目根目录
echo.

:: 检查Java环境
echo [1/5] 检查Java环境...
java -version >nul 2>&1
if errorlevel 1 (
    echo [X] 错误: 未检测到Java环境
    echo.
    echo 请先安装JDK 1.8或以上版本
    echo 下载地址: https://www.oracle.com/java/technologies/downloads/
    echo.
    pause
    exit /b 1
)
echo [√] Java环境检测通过
java -version
echo.

:: 检查Maven环境
echo [2/5] 检查Maven环境...
mvn -version >nul 2>&1
if errorlevel 1 (
    echo [X] 错误: 未检测到Maven环境
    echo.
    echo 请先安装Maven 3.0或以上版本
    echo 下载地址: https://maven.apache.org/download.cgi
    echo.
    pause
    exit /b 1
)
echo [√] Maven环境检测通过
mvn -version | findstr "Apache Maven"
echo.

:: 检查Node.js环境
echo [3/5] 检查Node.js环境...
node -v >nul 2>&1
if errorlevel 1 (
    echo [X] 错误: 未检测到Node.js环境
    echo.
    echo 请先安装Node.js 6.0或以上版本
    echo 下载地址: https://nodejs.org/
    echo.
    pause
    exit /b 1
)
echo [√] Node.js环境检测通过
node -v
echo.

:: 检查npm环境
echo [4/5] 检查npm环境...
npm -v >nul 2>&1
if errorlevel 1 (
    echo [X] 错误: 未检测到npm
    echo.
    echo 请确保Node.js安装正确
    echo.
    pause
    exit /b 1
)
echo [√] npm环境检测通过
npm -v
echo.

:: 检查pom.xml是否存在
echo [5/5] 检查项目文件...
if not exist "pom.xml" (
    echo [X] 错误: 未找到pom.xml文件
    echo.
    echo 请确保在项目根目录下运行此脚本
    echo 当前目录: %CD%
    echo.
    pause
    exit /b 1
)
echo [√] 项目文件检查通过
echo.

:: 检查前端目录
if not exist "UI\coursearrange" (
    echo [X] 错误: 未找到前端目录 UI\coursearrange
    echo.
    pause
    exit /b 1
)
echo [√] 前端目录检查通过
echo.

echo ========================================
echo    开始启动服务...
echo ========================================
echo.

:: 启动后端服务
echo [启动后端] 正在启动Spring Boot应用...
echo 命令: mvn spring-boot:run
echo.
start "智能排课系统-后端服务" cmd /k "cd /d "%~dp0" && echo 后端服务启动中... && mvn spring-boot:run"

if errorlevel 1 (
    echo [X] 后端启动命令执行失败
    pause
    exit /b 1
)

echo [√] 后端服务窗口已打开 (端口: 8080)
echo [提示] 后端启动需要时间，请等待...
echo.

:: 等待后端启动
echo 等待后端服务启动 (30秒)...
timeout /t 30 /nobreak
echo.

:: 启动前端服务
echo [启动前端] 正在启动Vue开发服务器...
echo.

:: 检查是否已安装依赖
if not exist "UI\coursearrange\node_modules" (
    echo [提示] 首次运行，需要安装前端依赖
    echo [提示] 这可能需要几分钟时间，请耐心等待...
    echo.
    cd /d "%~dp0UI\coursearrange"
    echo 正在执行: npm install
    call npm install
    if errorlevel 1 (
        echo.
        echo [X] 前端依赖安装失败
        echo [建议] 请手动执行: cd UI\coursearrange && npm install
        cd /d "%~dp0"
        pause
        exit /b 1
    )
    cd /d "%~dp0"
    echo.
    echo [√] 前端依赖安装完成
    echo.
)

echo 命令: npm run dev
echo.
start "智能排课系统-前端服务" cmd /k "cd /d "%~dp0UI\coursearrange" && echo 前端服务启动中... && npm run dev"

if errorlevel 1 (
    echo [X] 前端启动命令执行失败
    pause
    exit /b 1
)

echo [√] 前端服务窗口已打开 (端口: 8081)
echo [提示] 前端启动需要时间，请等待...
echo.

:: 等待前端启动
echo 等待前端服务启动 (15秒)...
timeout /t 15 /nobreak
echo.

echo ========================================
echo    启动完成！
echo ========================================
echo.
echo 服务信息:
echo   后端服务: http://localhost:8080
echo   前端服务: http://localhost:8081
echo   API文档:  http://localhost:8080/swagger-ui.html
echo.
echo 默认账号:
echo   管理员: admin / 123456
echo   教师:   teacher01 / 123456
echo   学生:   2020011234 / 123456
echo.
echo [提示] 浏览器将自动打开前端页面...
timeout /t 3 /nobreak >nul

:: 自动打开浏览器
start http://localhost:8081

echo.
echo [提示] 关闭此窗口不会停止服务
echo [提示] 如需停止服务，请关闭对应的命令行窗口
echo.
pause
