@echo off
echo 正在編譯 Auto Reconnect Plugin...

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

REM 編譯Java文件
echo 正在編譯Java源代碼...
javac -cp "lib/*" -d target/classes src/main/java/com/autodad/autoreconnect/*.java src/main/java/com/autodad/autoreconnect/commands/*.java

if errorlevel 1 (
    echo 編譯失敗！
    pause
    exit /b 1
)

REM 複製資源文件
echo 正在複製資源文件...
if not exist "target\classes\META-INF" mkdir target\classes\META-INF
copy "src\main\resources\velocity-plugin.json" "target\classes\velocity-plugin.json" >nul

REM 創建JAR文件
echo 正在創建JAR文件...
jar cf target/auto-reconnect-plugin.jar -C target/classes .

echo 編譯完成！JAR文件位於: target\auto-reconnect-plugin.jar
pause
