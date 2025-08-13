@echo off
echo 正在下載 Velocity 依賴庫（簡化版）...

REM 創建lib目錄
if not exist "lib" mkdir lib

REM 下載核心依賴
echo 下載 Velocity API...
curl -L -o lib/velocity-api.jar "https://repo1.maven.org/maven2/com/velocitypowered/velocity-api/3.4.0-SNAPSHOT/velocity-api-3.4.0-SNAPSHOT.jar"

echo 下載 SLF4J...
curl -L -o lib/slf4j-api.jar "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar"

echo 下載 Guice...
curl -L -o lib/guice.jar "https://repo1.maven.org/maven2/com/google/inject/guice/5.1.0/guice-5.1.0.jar"

echo 下載 Adventure...
curl -L -o lib/adventure-api.jar "https://repo1.maven.org/maven2/net/kyori/adventure/adventure-api/4.14.0/adventure-api-4.14.0.jar"

echo 依賴庫下載完成！
echo 現在可以運行 build-full.bat 來編譯完整版插件了
pause
