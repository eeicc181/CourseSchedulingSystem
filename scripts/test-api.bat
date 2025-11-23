@echo off
chcp 65001 >nul
echo ========================================
echo 测试后端API连接
echo ========================================
echo.

echo [1] 测试后端服务...
curl -s http://localhost:8080/classtask/1/0
echo.
echo.

echo [2] 测试学期接口...
curl -s http://localhost:8080/semester
echo.
echo.

echo ========================================
echo 测试完成
echo ========================================
pause
