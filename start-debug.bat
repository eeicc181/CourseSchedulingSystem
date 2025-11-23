@echo off
:: 调试版启动脚本 - 显示详细错误信息
chcp 65001
title 智能排课系统 - 调试启动

echo ========================================
echo    调试模式 - 详细诊断
echo ========================================
echo.

:: 显示当前目录
echo [诊断] 当前工作目录:
echo %CD%
echo.

:: 显示脚本目录
echo [诊断] 脚本所在目录:
echo %~dp0
echo.

:: 切换到脚本目录
echo [操作] 切换到脚本目录...
cd /d "%~dp0"
echo 切换后的目录: %CD%
echo.

:: 列出当前目录文件
echo [诊断] 当前目录文件列表:
dir /b
echo.

:: 检查关键文件
echo [检查] 检查关键文件是否存在...
if exist "pom.xml" (
    echo [√] pom.xml 存在
) else (
    echo [X] pom.xml 不存在 - 这是问题所在！
    echo.
    echo 请确保在项目根目录运行脚本
    echo 项目根目录应该包含: pom.xml, UI文件夹等
    echo.
    pause
    exit /b 1
)

if exist "UI\coursearrange" (
    echo [√] UI\coursearrange 目录存在
) else (
    echo [X] UI\coursearrange 目录不存在
)
echo.

:: 检查Java
echo [检查] Java环境...
java -version 2>&1
if errorlevel 1 (
    echo [X] Java未安装或未配置到PATH
    echo.
    echo 请安装JDK并配置环境变量
    pause
    exit /b 1
) else (
    echo [√] Java环境正常
)
echo.

:: 检查Maven
echo [检查] Maven环境...
mvn -version 2>&1
if errorlevel 1 (
    echo [X] Maven未安装或未配置到PATH
    echo.
    echo 请安装Maven并配置环境变量
    echo 或者使用项目自带的mvnw命令
    pause
    exit /b 1
) else (
    echo [√] Maven环境正常
)
echo.

:: 检查Node.js
echo [检查] Node.js环境...
node -v 2>&1
if errorlevel 1 (
    echo [X] Node.js未安装或未配置到PATH
    pause
    exit /b 1
) else (
    echo [√] Node.js环境正常
)
echo.

:: 检查npm
echo [检查] npm环境...
npm -v 2>&1
if errorlevel 1 (
    echo [X] npm未安装或未配置到PATH
    pause
    exit /b 1
) else (
    echo [√] npm环境正常
)
echo.

echo ========================================
echo    环境检查完成
echo ========================================
echo.
echo 所有环境检查通过！
echo.
echo 接下来您可以选择:
echo   1. 手动启动后端: mvn spring-boot:run
echo   2. 手动启动前端: cd UI\coursearrange && npm run dev
echo   3. 使用修复后的 start.bat 脚本
echo.
pause
