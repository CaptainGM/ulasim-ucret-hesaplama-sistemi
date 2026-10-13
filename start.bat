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

set "NEED_BUILD=0"
if not exist "target\classes\com\ulasim\hesaplama\Launcher.class" set "NEED_BUILD=1"
if not exist "target\dependency" set "NEED_BUILD=1"
if "%1"=="--build" set "NEED_BUILD=1"
if "%1"=="-b" set "NEED_BUILD=1"

if "!NEED_BUILD!"=="1" (
    echo [1/2] Uygulama derleniyor...
    call ".\mvnw.cmd" -q package
    if errorlevel 1 (
        echo.
        echo Derleme sirasinda hata olustu.
        pause
        exit /b 1
    )
    echo [2/2] Uygulama baslatiliyor...
) else (
    echo TransitRoute baslatiliyor...
)

"!JAVA_HOME!\bin\java.exe" -cp "target\classes;target\dependency\*" com.ulasim.hesaplama.Launcher

if errorlevel 1 (
    echo.
    echo Uygulama sonlandi.
)
