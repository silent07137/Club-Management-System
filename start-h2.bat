@echo off
setlocal
chcp 65001 >nul

if /I "%~1" NEQ "--inner" (
    start "Club System H2" cmd /k ""%~f0" --inner"
    exit /b
)

set "ROOT_DIR=%~dp0"
set "BACKEND_DIR=%ROOT_DIR%silent_backend"
set "FRONTEND_DIR=%ROOT_DIR%silent_frontend\club-system-frontend"

echo ==========================================
echo       Starting club system with H2...
echo ==========================================

echo [1/2] Starting backend (Spring Boot + H2)...
start "" /B /D "%BACKEND_DIR%" mvnw.cmd -Dspring-boot.run.profiles=h2 spring-boot:run

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
