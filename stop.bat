@echo off
setlocal
chcp 65001 >nul

set "ROOT_DIR=%~dp0"
if exist "%ROOT_DIR%club-launcher\bin\Release\net9.0-windows\ClubSystemLauncher.exe" (
    "%ROOT_DIR%club-launcher\bin\Release\net9.0-windows\ClubSystemLauncher.exe" --stop
) else (
    echo Launcher exe not found.
)

endlocal
