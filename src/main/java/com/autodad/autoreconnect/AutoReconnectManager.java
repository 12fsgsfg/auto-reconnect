package com.autodad.autoreconnect;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.sound.Sound;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class AutoReconnectManager {
    
    private final ProxyServer server;
    private final Logger logger;
    private final ServerManager serverManager;
    private final ScheduledExecutorService scheduler;
    private final AtomicBoolean running;
    
    // 重連配置
    private static final long CHECK_INTERVAL = 5; // 檢查間隔（秒）- 更頻繁
    private static final long RECONNECT_DELAY = 2; // 重連延遲（秒）- 更快
    private static final int MAX_RECONNECT_ATTEMPTS = 20; // 最大重連嘗試次數 - 更多
    
    private int reconnectAttempts = 0;
    
    public AutoReconnectManager(ProxyServer server, Logger logger, ServerManager serverManager) {
        this.server = server;
        this.logger = logger;
        this.serverManager = serverManager;
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.running = new AtomicBoolean(false);
    }
    
    public void start() {
        if (running.compareAndSet(false, true)) {
            logger.info("啟動自動重連管理器...");
            
            // 啟動定期檢查任務
            scheduler.scheduleAtFixedRate(
                this::checkAndReconnect,
                CHECK_INTERVAL,
                CHECK_INTERVAL,
                TimeUnit.SECONDS
            );
            
            logger.info("自動重連管理器已啟動，檢查間隔: {} 秒", CHECK_INTERVAL);
        }
    }
    
    public void stop() {
        if (running.compareAndSet(true, false)) {
            logger.info("正在停止自動重連管理器...");
            
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
            
            logger.info("自動重連管理器已停止");
        }
    }
    
    private void checkAndReconnect() {
        if (!running.get()) {
            return;
        }
        
        try {
            // 持續重連模式：不檢查狀態，直接嘗試重連
            // 只在debug模式下顯示詳細信息
            if (logger.isDebugEnabled()) {
                logger.debug("持續重連模式：嘗試重連Lobby服務器... (嘗試 {}/{})", 
                    reconnectAttempts + 1, MAX_RECONNECT_ATTEMPTS);
            }
            
            if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
                reconnectAttempts++;
                attemptReconnect();
            } else {
                logger.debug("重置重連計數器，繼續嘗試重連Lobby服務器");
                reconnectAttempts = 0;
                attemptReconnect();
            }
        } catch (Exception e) {
            logger.error("重連過程中發生錯誤", e);
        }
    }
    
    private void attemptReconnect() {
        try {
            // 延遲重連
            scheduler.schedule(() -> {
                if (running.get()) {
                    logger.debug("正在嘗試重連Lobby服務器...");
                    
                    // 刷新服務器註冊
                    serverManager.refreshServer(ServerManager.LOBBY_SERVER);
                    
                    // 等待一下讓服務器狀態更新
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    
                    // 持續重連模式：不檢查結果，繼續嘗試
                    logger.debug("Lobby服務器重連嘗試完成，將在下次檢查時繼續嘗試");
                    
                    // 嘗試將wait分流的玩家轉移到lobby
                    attemptTransferPlayersFromWaitToLobby();
                }
            }, RECONNECT_DELAY, TimeUnit.SECONDS);
            
        } catch (Exception e) {
            logger.error("重連過程中發生錯誤", e);
        }
    }
    
    public void forceReconnect() {
        logger.info("強制重連Lobby服務器...");
        reconnectAttempts = 0;
        attemptReconnect();
    }
    
    public int getReconnectAttempts() {
        return reconnectAttempts;
    }
    
    public boolean isRunning() {
        return running.get();
    }
    
    private boolean hasPlayersOnLobby() {
        try {
            Optional<RegisteredServer> lobbyServer = serverManager.getLobbyServer();
            if (lobbyServer.isPresent()) {
                return lobbyServer.get().getPlayersConnected().size() > 0;
            }
        } catch (Exception e) {
            logger.debug("檢查lobby玩家數量時發生錯誤: {}", e.getMessage());
        }
        return false;
    }
    
    private void attemptTransferPlayersFromWaitToLobby() {
        try {
            Optional<RegisteredServer> waitServer = serverManager.getWaitServer();
            Optional<RegisteredServer> lobbyServer = serverManager.getLobbyServer();
            
            if (waitServer.isPresent() && lobbyServer.isPresent()) {
                // 獲取wait分流的玩家
                Collection<Player> playersOnWait = waitServer.get().getPlayersConnected();
                
                if (!playersOnWait.isEmpty()) {
                    logger.info("發現 {} 個玩家在wait分流，嘗試轉移到lobby", playersOnWait.size());
                    
                    // 嘗試將每個玩家轉移到lobby
                    for (Player player : playersOnWait) {
                        try {
                            // 發送斷線通知給玩家
                            sendDisconnectNotification(player);
                            
                            // 創建連接到lobby的請求
                            lobbyServer.get().ping().thenAccept(ping -> {
                                // 如果ping成功，嘗試轉移玩家
                                player.createConnectionRequest(lobbyServer.get()).fireAndForget();
                                logger.info("已嘗試將玩家 {} 從wait轉移到lobby", player.getUsername());
                            }).exceptionally(throwable -> {
                                logger.debug("無法ping lobby服務器: {}", throwable.getMessage());
                                return null;
                            });
                        } catch (Exception e) {
                            logger.debug("轉移玩家 {} 時發生錯誤: {}", player.getUsername(), e.getMessage());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("嘗試轉移玩家時發生錯誤: {}", e.getMessage());
        }
    }
    
    private void sendDisconnectNotification(Player player) {
        try {
            // 發送屏幕中央標題
            Component title = Component.text("⚠️ 伺服器斷線啦!", NamedTextColor.RED);
            Component subtitle = Component.text("正在嘗試重連...", NamedTextColor.YELLOW);
            
            Title titleObj = Title.title(title, subtitle, 
                Title.Times.times(
                    java.time.Duration.ofMillis(500),    // 淡入時間
                    java.time.Duration.ofMillis(3000),   // 顯示時間
                    java.time.Duration.ofMillis(500)     // 淡出時間
                ));
            
            player.showTitle(titleObj);
            
            // 暫時移除音效，避免編譯錯誤
            // TODO: 實現音效功能
            
            // 發送聊天欄消息
            Component chatMessage = Component.text()
                .append(Component.text("[系統] ", NamedTextColor.GOLD))
                .append(Component.text("Lobby服務器斷線，正在嘗試重連...", NamedTextColor.WHITE))
                .build();
            
            player.sendMessage(chatMessage);
            
            logger.debug("已向玩家 {} 發送斷線通知", player.getUsername());
            
        } catch (Exception e) {
            logger.debug("發送通知給玩家 {} 時發生錯誤: {}", player.getUsername(), e.getMessage());
        }
    }
}
