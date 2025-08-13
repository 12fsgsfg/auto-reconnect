@echo off
echo 測試簡化版 Auto Reconnect Plugin...
echo.
echo 插件將運行75秒進行測試：
echo - 前15秒：服務器在線
echo - 接下來60秒：服務器離線，觸發自動重連
echo.
echo 按任意鍵開始測試...
pause >nul

java -cp target\simple-auto-reconnect-plugin.jar com.autodad.autoreconnect.SimpleAutoReconnectPlugin

echo.
echo 測試完成！
pause
