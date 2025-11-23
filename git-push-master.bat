@echo off
chcp 65001 >nul
echo ========================================
echo 推送代码到 Gitee 远程仓库 (master 分支)
echo ========================================
echo.
echo 远程仓库: https://gitee.com/low-key123/software-project.git
echo 分支: master
echo.
echo 正在推送...
echo.

git push -u origin master

echo.
echo ========================================
if %ERRORLEVEL% EQU 0 (
    echo ✓ 推送成功！
) else (
    echo ✗ 推送失败，请检查网络连接和仓库权限
)
echo ========================================
pause
