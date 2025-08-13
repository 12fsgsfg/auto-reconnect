package com.autodad.autoreconnect;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * 簡化版的自動重連插件
 * 這個版本不依賴Velocity API，可以獨立編譯和測試
 */
public class SimpleAutoReconnectPlugin {
    
    private static final Logger logger = Logger.getLogger(SimpleAutoReconnectPlugin.class.getName());
    private final ScheduledExecutorService scheduler;
    private final AtomicBoolean running;
    
    // 重連配置
    private static final long CHECK_INTERVAL = 10; // 檢查間隔（秒）
    private static final long RECONNECT_DELAY = 5; // 重連延遲（秒）
    private static final int MAX_RECONNECT_ATTEMPTS = 10; // 最大重連嘗試次數
    
    private int reconnectAttempts = 0;
    private boolean waitServerOnline = true; // 模擬服務器狀態
    
    public SimpleAutoReconnectPlugin() {
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.running = new AtomicBoolean(false);
    }
    
    public void start() {
        if (running.compareAndSet(false, true)) {
            logger.info("啟動簡化版自動重連管理器...");
            
            // 啟動定期檢查任務
            scheduler.scheduleAtFixedRate(
                this::checkAndReconnect,
                CHECK_INTERVAL,
                CHECK_INTERVAL,
                TimeUnit.SECONDS
            );
            
            logger.info("簡化版自動重連管理器已啟動，檢查間隔: " + CHECK_INTERVAL + " 秒");
        }
    }
    
    public void stop() {
        if (running.compareAndSet(true, false)) {
            logger.info("正在停止簡化版自動重連管理器...");
            
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
            
            logger.info("簡化版自動重連管理器已停止");
        }
    }
    
    private void checkAndReconnect() {
        if (!running.get()) {
            return;
        }
        
        try {
            // 模擬檢查wait服務器狀態
            if (!isWaitServerOnline()) {
                logger.warning("Wait服務器離線，嘗試重連... (嘗試 " + 
                    (reconnectAttempts + 1) + "/" + MAX_RECONNECT_ATTEMPTS + ")");
                
                if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
                    reconnectAttempts++;
                    attemptReconnect();
                } else {
                    logger.severe("Wait服務器重連失敗，已達到最大嘗試次數: " + MAX_RECONNECT_ATTEMPTS);
                    // 重置計數器，繼續嘗試
                    reconnectAttempts = 0;
                }
            } else {
                // 服務器在線，重置重連計數器
                if (reconnectAttempts > 0) {
                    logger.info("Wait服務器已恢復連線，重連成功！");
                    reconnectAttempts = 0;
                }
            }
        } catch (Exception e) {
            logger.severe("檢查服務器狀態時發生錯誤: " + e.getMessage());
        }
    }
    
    private void attemptReconnect() {
        try {
            // 延遲重連
            scheduler.schedule(() -> {
                if (running.get()) {
                    logger.info("正在嘗試重連Wait服務器...");
                    
                    // 模擬重連過程
                    simulateReconnect();
                    
                    // 檢查重連是否成功
                    if (isWaitServerOnline()) {
                        logger.info("Wait服務器重連成功！");
                        reconnectAttempts = 0;
                    } else {
                        logger.warning("Wait服務器重連失敗，將在下次檢查時重試");
                    }
                }
            }, RECONNECT_DELAY, TimeUnit.SECONDS);
            
        } catch (Exception e) {
            logger.severe("重連過程中發生錯誤: " + e.getMessage());
        }
    }
    
    private void simulateReconnect() {
        // 模擬重連過程，有50%的成功率
        if (Math.random() > 0.5) {
            waitServerOnline = true;
            logger.info("模擬重連成功");
        } else {
            logger.warning("模擬重連失敗");
        }
    }
    
    public void forceReconnect() {
        logger.info("強制重連Wait服務器...");
        reconnectAttempts = 0;
        attemptReconnect();
    }
    
    public void simulateServerOffline() {
        logger.info("模擬Wait服務器離線...");
        waitServerOnline = false;
    }
    
    public void simulateServerOnline() {
        logger.info("模擬Wait服務器上線...");
        waitServerOnline = true;
    }
    
    public boolean isWaitServerOnline() {
        return waitServerOnline;
    }
    
    public int getReconnectAttempts() {
        return reconnectAttempts;
    }
    
    public boolean isRunning() {
        return running.get();
    }
    
    // 測試方法
    public static void main(String[] args) {
        SimpleAutoReconnectPlugin plugin = new SimpleAutoReconnectPlugin();
        
        // 啟動插件
        plugin.start();
        
        // 模擬服務器離線
        try {
            Thread.sleep(15000); // 等待15秒
            plugin.simulateServerOffline();
            
            // 讓插件運行一段時間
            Thread.sleep(60000); // 運行1分鐘
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // 停止插件
            plugin.stop();
        }
        
        System.out.println("測試完成！");
    }
}
