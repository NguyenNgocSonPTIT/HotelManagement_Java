@echo off
chcp 65001 >nul
title HỆ THỐNG QUẢN LÝ KHÁCH SẠN (JAVA SWING & MYSQL)
cd /d "%~dp0"
echo ==============================================================
echo [JAVA HMS] DANG KHOI CHAY UNG DUNG QUAN LY KHACH SAN...
echo ==============================================================

:: 1. Kiem tra xem may da cai Java chua
where java >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [LOI] May tinh cua ban chua cai dat Java (JDK/JRE) hoac chua them vao bien moi truong PATH!
    echo Vui long cai dat JDK (Java 17 hoac Java 21) de chay ung dung.
    echo ==============================================================
    pause
    exit /b
)

:: 2. Neu thu muc bin chua co hoac chua co file class, tu dong bien dich
if not exist "bin\hotel\MainFrame.class" (
    echo.
    echo [THONG BAO] Phat hien ma nguon chua duoc bien dich (tai tu Git ve).
    echo Dang tu dong bien dich toan bo ma nguon Java...
    if not exist "bin" mkdir bin
    where javac >nul 2>&1
    if %ERRORLEVEL% EQU 0 (
        javac -encoding UTF-8 -cp "lib/mysql-connector-j-8.3.0.jar" -d bin src/hotel/*.java
        if %ERRORLEVEL% NEQ 0 (
            echo.
            echo [LOI] Bien dich that bai! Vui long kiem tra lai JDK.
            pause
            exit /b
        )
        echo [OK] Bien dich thanh cong!
    ) else (
        echo [LOI] Khong tim thay trinh bien dich javac tren may!
        echo Ban can cai dat JDK (Java Development Kit) thay vi chi cai moi JRE.
        pause
        exit /b
    )
)

:: 3. Khoi chay ung dung
echo.
echo Dang khoi chay giao dien he thong...
java -cp "bin;lib/mysql-connector-j-8.3.0.jar" hotel.MainFrame
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ==============================================================
    echo [THONG BAO] Chuong trinh vua ket thuc hoac xay ra loi.
    echo Neu bao loi ket noi MySQL: Hay mo Kiem_Tra_Ket_Noi_MySQL.bat de kiem tra.
    echo ==============================================================
    pause
)
