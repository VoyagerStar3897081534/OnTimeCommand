@echo off
echo Starting Minecraft Server...
echo Make sure you have installed Java 21 or higher.
echo.

REM 检查是否存在服务器jar文件
if not exist "paper-1.21.4.jar" (
    echo Paper 1.21.4 server jar not found.
    echo.
    pause
    exit /b 1
)

REM 启动Minecraft服务器
echo Starting Paper server...
java -Xms2G -Xmx2G -jar paper-1.21.4.jar