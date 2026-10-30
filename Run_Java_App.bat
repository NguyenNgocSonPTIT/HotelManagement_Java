@echo off
chcp 65001 >nul
title HỆ THỐNG QUẢN LÝ KHÁCH SẠN (JAVA SWING & MYSQL)
echo ==============================================================
echo [JAVA HMS] DANG KHOI CHAY UNG DUNG QUAN LY KHACH SAN...
echo ==============================================================
cd /d "%~dp0"
start javaw -cp "bin;lib/mysql-connector-j-8.3.0.jar" hotel.MainFrame
if %ERRORLEVEL% NEQ 0 (
    echo.
    java -cp "bin;lib/mysql-connector-j-8.3.0.jar" hotel.MainFrame
    pause
)
