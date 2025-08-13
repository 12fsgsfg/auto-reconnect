package com.autodad.autoreconnect;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.autodad.autoreconnect.commands.ReconnectCommand;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Plugin(
    id = "auto-reconnect",
    name = "Auto Reconnect Plugin",
    version = "1.0.0",
    description = "Velocity plugin with lobby and wait servers, auto-reconnect for wait server",
    authors = {"AutoDad"}
)
public class AutoReconnectPlugin {

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private final ServerManager serverManager;
    private final AutoReconnectManager reconnectManager;

    @Inject
    public AutoReconnectPlugin(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.serverManager = new ServerManager(server, logger);
        this.reconnectManager = new AutoReconnectManager(server, logger, serverManager);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("Auto Reconnect Plugin 正在啟動...");
        
        // 初始化服務器管理器
        serverManager.initialize();
        
        // 啟動自動重連管理器
        reconnectManager.start();
        
        // 註冊命令
        registerCommands();
        
        logger.info("Auto Reconnect Plugin 啟動完成！");
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        logger.info("Auto Reconnect Plugin 正在關閉...");
        
        // 停止自動重連管理器
        reconnectManager.stop();
        
        logger.info("Auto Reconnect Plugin 已關閉！");
    }
    
    private void registerCommands() {
        CommandManager commandManager = server.getCommandManager();
        
        // 註冊重連命令
        ReconnectCommand reconnectCommand = new ReconnectCommand(server, reconnectManager, serverManager);
        commandManager.register("reconnect", reconnectCommand, "recon", "rc");
        
        logger.info("已註冊命令: /reconnect, /recon, /rc");
    }
}
