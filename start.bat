@echo off
chcp 65001 >nul
echo ==========================================
echo       🚀 正在启动社团管理系统...
echo ==========================================

:: 自动识别你提供的真实目录路径
set BACKEND_DIR=silent_backend
set FRONTEND_DIR=silent_frontend\club-system-frontend

echo [1/2] 正在启动后端 (Spring Boot)...
:: 如果你的电脑没配置 mvn 环境变量，可以把下面的 mvn 换成 .\mvnw.cmd
start "社团后端服务" cmd /k "cd %BACKEND_DIR% && .\mvnw.cmd spring-boot:run"

echo [2/2] 正在启动前端 (Vue 3)...
start "社团前端服务" cmd /k "cd %FRONTEND_DIR% && npm run dev"

echo.
echo ⏳ 正在等待服务初始化，5秒后将自动打开浏览器...
timeout /t 10 /nobreak >nul

echo 🌐 正在呼出浏览器...
:: 默认调起默认浏览器访问前端地址
start http://localhost:5173

echo ✅ 启动指令已全部执行！
echo ==========================================
pause