@echo off
chcp 65001 >nul
title KIỂM TRA KẾT NỐI CƠ SỞ DỮ LIỆU MYSQL
cd /d "%~dp0"
java -cp "bin;lib/mysql-connector-j-8.3.0.jar" hotel.TestConnection
pause
