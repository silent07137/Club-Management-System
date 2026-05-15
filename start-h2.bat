@echo off
setlocal
chcp 65001 >nul

set "ROOT_DIR=%~dp0"
set "BACKEND_DIR=%ROOT_DIR%silent_backend"
set "FRONTEND_DIR=%ROOT_DIR%silent_frontend\club-system-frontend"

echo ==========================================
echo       Starting club system with H2...
echo ==========================================

echo [1/2] Starting backend (Spring Boot + H2)...
start "" /D "%BACKEND_DIR%" cmd /k call mvnw.cmd -Dspring-boot.run.profiles=h2 spring-boot:run

echo [2/2] Starting frontend (Vue 3)...
start "" /D "%FRONTEND_DIR%" cmd /k npm run dev

echo.
echo Waiting for startup, browser will open shortly...
timeout /t 10 /nobreak >nul

start "" http://localhost:5173

echo Startup commands have been executed.
echo ==========================================
endlocal
pause
