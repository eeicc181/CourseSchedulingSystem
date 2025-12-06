@echo off
chcp 65001
echo ========================================
echo 执行所有 SQL 优化脚本
echo ========================================
echo.

set MYSQL_HOST=localhost
set MYSQL_PORT=3306
set MYSQL_USER=root
set MYSQL_PASSWORD=575233
set MYSQL_DATABASE=db_course_arrangement

echo [1/2] 正在执行索引优化脚本...
mysql -h%MYSQL_HOST% -P%MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PASSWORD% %MYSQL_DATABASE% < index_optimization.sql
if %errorlevel% neq 0 (
    echo 错误：索引优化脚本执行失败！
    pause
    exit /b 1
)
echo ✓ 索引优化脚本执行成功！
echo.

echo [2/2] 正在执行密码迁移脚本...
echo 注意：此脚本会将所有明文密码重置为 BCrypt 加密的 "123456"
echo 按任意键继续，或关闭窗口取消...
pause

mysql -h%MYSQL_HOST% -P%MYSQL_PORT% -u%MYSQL_USER% -p%MYSQL_PASSWORD% %MYSQL_DATABASE% < password_migration.sql
if %errorlevel% neq 0 (
    echo 错误：密码迁移脚本执行失败！
    pause
    exit /b 1
)
echo ✓ 密码迁移脚本执行成功！
echo.

echo ========================================
echo 所有 SQL 脚本执行完成！
echo ========================================
echo.
echo 执行结果：
echo - 索引优化：已完成
echo - 密码迁移：已完成
echo.
echo 提示：
echo 1. 所有用户密码已重置为：123456
echo 2. 数据库查询性能已优化
echo 3. 建议重启应用以应用更改
echo.
pause
