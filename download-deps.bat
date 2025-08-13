@echo off
echo 正在下載 Velocity 依賴庫...

REM 創建臨時目錄
if not exist "temp" mkdir temp
cd temp

REM 下載 Velocity API
echo 下載 Velocity API...
curl -L -o velocity-api.jar "https://repo1.maven.org/maven2/com/velocitypowered/velocity-api/3.3.0-SNAPSHOT/velocity-api-3.3.0-SNAPSHOT.jar"

REM 下載 Velocity Proxy
echo 下載 Velocity Proxy...
curl -L -o velocity-proxy.jar "https://repo1.maven.org/maven2/com/velocitypowered/velocity-proxy/3.3.0-SNAPSHOT/velocity-proxy-3.3.0-SNAPSHOT.jar"

REM 下載 SLF4J
echo 下載 SLF4J...
curl -L -o slf4j-api.jar "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar"

REM 下載 Guice
echo 下載 Guice...
curl -L -o guice.jar "https://repo1.maven.org/maven2/com/google/inject/guice/5.1.0/guice-5.1.0.jar"

REM 下載 Adventure
echo 下載 Adventure...
curl -L -o adventure-api.jar "https://repo1.maven.org/maven2/net/kyori/adventure/adventure-api/4.14.0/adventure-api-4.14.0.jar"
curl -L -o adventure-text-serializer-gson.jar "https://repo1.maven.org/maven2/net/kyori/adventure/adventure-text-serializer-gson/4.14.0/adventure-text-serializer-gson-4.14.0.jar"

REM 移動到lib目錄
cd ..
move temp\*.jar lib\

REM 清理臨時目錄
rmdir /s /q temp

echo 依賴庫下載完成！
echo 現在可以運行 build.bat 來編譯插件了
pause
