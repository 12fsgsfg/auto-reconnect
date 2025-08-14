package com.autodad.autoreconnect;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ServerManager {
    
    private final ProxyServer server;
    private final Logger logger;
    private final ConfigManager configManager;
    private final ConcurrentMap<String, RegisteredServer> servers;
    
    public ServerManager(ProxyServer server, Logger logger, ConfigManager configManager) {
        this.server = server;
        this.logger = logger;
        this.configManager = configManager;
        this.servers = new ConcurrentHashMap<>();
    }
    
    public void initialize() {
        logger.info("正在初始化服務器管理器...");
        
        // 註冊服務器
        registerServer(configManager.getLobbyServerName());
        registerServer(configManager.getWaitServerName());
        
        logger.info("服務器管理器初始化完成");
    }
    
    private void registerServer(String serverName) {
        Optional<RegisteredServer> registeredServer = server.getServer(serverName);
        if (registeredServer.isPresent()) {
            servers.put(serverName, registeredServer.get());
            logger.debug("成功註冊服務器: {}", serverName);
        } else {
            logger.warn("無法找到服務器: {}", serverName);
        }
    }
    
    public Optional<RegisteredServer> getServer(String serverName) {
        return Optional.ofNullable(servers.get(serverName));
    }
    
    public Optional<RegisteredServer> getLobbyServer() {
        return getServer(configManager.getLobbyServerName());
    }
    
    public Optional<RegisteredServer> getWaitServer() {
        return getServer(configManager.getWaitServerName());
    }
    
    public boolean isServerOnline(String serverName) {
        // 首先檢查服務器是否在我們的註冊列表中
        Optional<RegisteredServer> server = getServer(serverName);
        if (server.isPresent()) {
            // 檢查服務器是否真的可用
            try {
                RegisteredServer registeredServer = server.get();
                // 使用ping來檢查服務器狀態，但設置超時
                return registeredServer.ping().isDone();
            } catch (Exception e) {
                logger.debug("檢查服務器 {} 狀態時發生錯誤: {}", serverName, e.getMessage());
                return false;
            }
        }
        
        // 如果不在我們的列表中，嘗試從Velocity重新獲取
        Optional<RegisteredServer> velocityServer = this.server.getServer(serverName);
        if (velocityServer.isPresent()) {
            logger.debug("服務器 {} 在Velocity中可用，重新註冊", serverName);
            registerServer(serverName);
            // 重新檢查狀態
            return isServerOnline(serverName);
        }
        
        logger.debug("服務器 {} 不可用", serverName);
        return false;
    }
    
    public boolean isLobbyOnline() {
        // 使用多種方法檢查lobby服務器狀態
        String lobbyName = configManager.getLobbyServerName();
        boolean online = isServerOnline(lobbyName);
        
        // 如果ping檢查失敗，嘗試其他方法
        if (!online) {
            // 檢查是否有玩家連接到lobby
            online = hasPlayersOnServer(lobbyName);
            if (online) {
                logger.debug("Lobby服務器通過玩家連接檢測為在線");
            }
        }
        
        logger.debug("Lobby服務器狀態檢查: {} (名稱: {})", online, lobbyName);
        return online;
    }
    
    private boolean hasPlayersOnServer(String serverName) {
        try {
            Optional<RegisteredServer> server = getServer(serverName);
            if (server.isPresent()) {
                // 檢查是否有玩家在該服務器上
                return server.get().getPlayersConnected().size() > 0;
            }
        } catch (Exception e) {
            logger.debug("檢查服務器 {} 玩家數量時發生錯誤: {}", serverName, e.getMessage());
        }
        return false;
    }
    
    public boolean isWaitOnline() {
        return isServerOnline(configManager.getWaitServerName());
    }
    
    public void refreshServer(String serverName) {
        servers.remove(serverName);
        registerServer(serverName);
    }
    
    public void refreshAllServers() {
        logger.info("正在刷新所有服務器...");
        servers.clear();
        registerServer(configManager.getLobbyServerName());
        registerServer(configManager.getWaitServerName());
    }
}
