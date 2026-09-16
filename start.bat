@echo off
setlocal enabledelayedexpansion
chcp 65001 >nul
cd /d "%~dp0"

if not defined JAVA_HOME (
    for /f "tokens=1,* delims==" %%A in ('java -XshowSettings:properties -version 2^>^&1 ^| findstr /C:"java.home"') do set "JAVA_HOME=%%B"
    for /f "tokens=* delims= " %%C in ("!JAVA_HOME!") do set "JAVA_HOME=%%C"
)

if not defined JAVA_HOME (
    echo Java bulunamadi. Lutfen JDK 21 veya uzerini kurup PATH'e ekleyin.
    pause
    exit /b 1
)

echo [1/2] Uygulama derleniyor (ilk calistirmada bagimliliklar indirilecegi icin biraz surebilir)...
call ".\mvnw.cmd" -q package
if errorlevel 1 (
    echo.
    echo Derleme sirasinda hata olustu. Yukaridaki mesajlari kontrol edin.
    pause
    exit /b 1
)

echo [2/2] Uygulama baslatiliyor...
"!JAVA_HOME!\bin\java.exe" -cp "target\classes;target\dependency\*" com.ulasim.hesaplama.Launcher

if errorlevel 1 (
    echo.
    echo Uygulama calisirken bir hata olustu.
    pause
)
