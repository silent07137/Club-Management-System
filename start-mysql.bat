@echo off
setlocal
chcp 65001 >nul

if /I "%~1" NEQ "--inner" (
    start "Club System MySQL" cmd /k ""%~f0" --inner"
    exit /b
)

set "ROOT_DIR=%~dp0"
set "BACKEND_DIR=%ROOT_DIR%silent_backend"
set "FRONTEND_DIR=%ROOT_DIR%silent_frontend\club-system-frontend"

echo ==========================================
echo       Starting club system with MySQL...
echo ==========================================

netstat -ano | findstr ":3306" >nul
if errorlevel 1 goto MYSQL_NOT_RUNNING

echo [1/2] Starting backend (Spring Boot + MySQL)...
start "" /B /D "%BACKEND_DIR%" mvnw.cmd -Dspring-boot.run.profiles=mysql spring-boot:run

echo [2/2] Starting frontend (Vue 3)...
start "" /B /D "%FRONTEND_DIR%" npm run dev

echo.
echo Waiting for startup, browser will open shortly...
timeout /t 10 /nobreak >nul

start "" http://localhost:5173

echo Startup commands have been executed.
echo ==========================================
endlocal
pause

:MYSQL_NOT_RUNNING
echo MySQL 3306 端口未监听，请先启动本机 MySQL80 服务。
echo 如果你想直接体验系统，请改用 start.bat 启动 H2 版本。
endlocal
pause
exit /b 1
