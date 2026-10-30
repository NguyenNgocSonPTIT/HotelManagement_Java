@echo off
chcp 65001 >nul
title KHỞI ĐỘNG DỊCH VỤ MYSQL SERVER
echo ==============================================================
echo [MYSQL] DANG KHOI DONG DICH VU MYSQL80...
echo ==============================================================
net session >nul 2>&1
if %errorLevel% NEQ 0 (
    echo [CANH BAO] Ban can chay file nay bang quyen Administrator:
    echo 1. Nhan CHUOT PHAI vao file Bat_Dich_Vu_MySQL.bat nay
    echo 2. Chon "Run as administrator" (Chay voi tu cach quan tri vien)
    echo ==============================================================
    pause
    exit /b
)

net start MySQL80
if %errorLevel% EQU 0 (
    echo.
    echo [THANH CONG] Dich vu MySQL80 da duoc bat thanh cong!
    echo Bay gio ban co the mo file Run_Java_App.bat de chay ung dung.
) else (
    echo.
    echo [THONG BAO] Neu MySQL bao loi hoac khong tim thay dich vu:
    echo Ban hay mo Services (Nhan Win+R, go services.msc, tim MySQL80 roi nhan Start).
)
echo ==============================================================
pause
