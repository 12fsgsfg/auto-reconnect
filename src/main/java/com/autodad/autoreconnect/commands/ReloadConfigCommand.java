package com.autodad.autoreconnect.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.Player;
import com.autodad.autoreconnect.ConfigManager;
import com.autodad.autoreconnect.ServerManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.slf4j.Logger;

public class ReloadConfigCommand implements SimpleCommand {
    
    private final ProxyServer server;
    private final Logger logger;
    private final ConfigManager configManager;
    private final ServerManager serverManager;
    
    public ReloadConfigCommand(ProxyServer server, Logger logger, ConfigManager configManager, ServerManager serverManager) {
        this.server = server;
        this.logger = logger;
        this.configManager = configManager;
        this.serverManager = serverManager;
    }
    
    @Override
    public void execute(Invocation invocation) {
        if (!(invocation.source() instanceof Player)) {
            // 控制台執行
            reloadConfig();
            return;
        }
        
        Player player = (Player) invocation.source();
        
        // 檢查權限
        if (!player.hasPermission("autoreconnect.reload")) {
            player.sendMessage(Component.text("您沒有權限執行此命令！", NamedTextColor.RED));
            return;
        }
        
        reloadConfig();
        player.sendMessage(Component.text("配置已重新載入！", NamedTextColor.GREEN));
    }
    
    private void reloadConfig() {
        try {
            logger.info("正在重新載入配置文件...");
            
            // 重新載入配置
            configManager.reloadConfig();
            
            // 刷新伺服器管理器
            serverManager.refreshAllServers();
            
            logger.info("配置文件重新載入成功");
        } catch (Exception e) {
            logger.error("重新載入配置文件時發生錯誤: {}", e.getMessage());
        }
    }
    
    @Override
    public boolean hasPermission(Invocation invocation) {
        if (invocation.source() instanceof Player) {
            return ((Player) invocation.source()).hasPermission("autoreconnect.reload");
        }
        return true; // 控制台總是有權限
    }
}
