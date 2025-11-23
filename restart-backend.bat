@echo off
chcp 65001 >nul
echo ========================================
echo 清理并重启后端服务
echo ========================================
echo.

echo [1] 清理编译缓存...
call mvn clean
echo.

echo [2] 重新编译...
call mvn compile
echo.

echo [3] 启动后端服务...
echo 后端将运行在 http://localhost:8080
echo 按 Ctrl+C 可停止服务
echo.
call mvn spring-boot:run

pause
