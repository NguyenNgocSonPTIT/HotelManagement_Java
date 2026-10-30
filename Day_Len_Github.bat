@echo off
chcp 65001 >nul
title ĐẨY TOÀN BỘ MÃ NGUỒN LÊN GITHUB
cd /d "%~dp0"
echo ==============================================================
echo [GIT] DANG TIEN HANH DAY CODE LEN GITHUB...
echo Repo: https://github.com/NguyenNgocSonPTIT/HotelManagement_Java.git
echo ==============================================================
"C:\Program Files\Git\cmd\git.exe" push -u origin main
if %ERRORLEVEL% EQU 0 (
    echo.
    echo ==============================================================
    echo [THANH CONG] MA NGUON DA DUOC DAY LEN GITHUB THANH CONG!
    echo Ban co the truy cap vao: https://github.com/NguyenNgocSonPTIT/HotelManagement_Java
    echo ==============================================================
) else (
    echo.
    echo [HUONG DAN] Neu trinh duyet hien ra cua so dang nhap GitHub:
    echo Ban chi can bam "Sign in with your browser" de xac nhan.
)
echo.
pause
