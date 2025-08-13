@echo off
echo 正在編譯簡化版 Auto Reconnect Plugin...

REM 檢查Java是否可用
java -version >nul 2>&1
if errorlevel 1 (
    echo 錯誤: 找不到Java，請確保Java已安裝並在PATH中
    pause
    exit /b 1
)

REM 創建目標目錄
if not exist "target" mkdir target
if not exist "target\classes" mkdir target\classes

REM 編譯簡化版Java文件
echo 正在編譯簡化版Java源代碼...
javac -d target/classes src/main/java/com/autodad/autoreconnect/SimpleAutoReconnectPlugin.java

if errorlevel 1 (
    echo 編譯失敗！
    pause
    exit /b 1
)

REM 創建JAR文件
echo 正在創建JAR文件...
jar cf target/simple-auto-reconnect-plugin.jar -C target/classes .

echo 簡化版編譯完成！JAR文件位於: target\simple-auto-reconnect-plugin.jar
echo.
echo 您可以運行以下命令來測試插件：
echo java -cp target\simple-auto-reconnect-plugin.jar com.autodad.autoreconnect.SimpleAutoReconnectPlugin
echo.
pause
