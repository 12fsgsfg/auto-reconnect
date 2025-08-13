package com.autodad.autoreconnect.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.autodad.autoreconnect.AutoReconnectManager;
import com.autodad.autoreconnect.ServerManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;
import java.util.Optional;

public class ReconnectCommand implements SimpleCommand {
    
    private final ProxyServer server;
    private final AutoReconnectManager reconnectManager;
    private final ServerManager serverManager;
    
    public ReconnectCommand(ProxyServer server, AutoReconnectManager reconnectManager, ServerManager serverManager) {
        this.server = server;
        this.reconnectManager = reconnectManager;
        this.serverManager = serverManager;
    }
    
    @Override
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();
        
        if (args.length == 0) {
            showHelp(invocation);
            return;
        }
        
        switch (args[0].toLowerCase()) {
            case "status":
                showStatus(invocation);
                break;
            case "reconnect":
                if (args.length > 1 && args[1].equalsIgnoreCase("force")) {
                    forceReconnect(invocation);
                } else {
                    showReconnectHelp(invocation);
                }
                break;
            case "servers":
                showServersStatus(invocation);
                break;
            case "help":
            default:
                showHelp(invocation);
                break;
        }
    }
    
    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        
        if (args.length == 1) {
            return List.of("status", "reconnect", "servers", "help");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("reconnect")) {
            return List.of("force");
        }
        
        return List.of();
    }
    
    private void showHelp(Invocation invocation) {
        Component help = Component.text()
            .append(Component.text("=== Auto Reconnect Plugin 幫助 ===", NamedTextColor.GOLD))
            .append(Component.newline())
            .append(Component.text("/reconnect status", NamedTextColor.YELLOW))
            .append(Component.text(" - 顯示自動重連狀態", NamedTextColor.WHITE))
            .append(Component.newline())
            .append(Component.text("/reconnect reconnect force", NamedTextColor.YELLOW))
            .append(Component.text(" - 強制重連Lobby服務器", NamedTextColor.WHITE))
            .append(Component.newline())
            .append(Component.text("/reconnect servers", NamedTextColor.YELLOW))
            .append(Component.text(" - 顯示所有服務器狀態", NamedTextColor.WHITE))
            .append(Component.newline())
            .append(Component.text("/reconnect help", NamedTextColor.YELLOW))
            .append(Component.text(" - 顯示此幫助", NamedTextColor.WHITE))
            .build();
        
        sendMessage(invocation, help);
    }
    
    private void showReconnectHelp(Invocation invocation) {
        Component help = Component.text()
            .append(Component.text("用法: /reconnect reconnect force", NamedTextColor.YELLOW))
            .append(Component.newline())
            .append(Component.text("此命令將強制嘗試重連Wait服務器", NamedTextColor.WHITE))
            .build();
        
        sendMessage(invocation, help);
    }
    
    private void showStatus(Invocation invocation) {
        Component status = Component.text()
            .append(Component.text("=== 自動重連狀態 ===", NamedTextColor.GOLD))
            .append(Component.newline())
            .append(Component.text("運行狀態: ", NamedTextColor.YELLOW))
            .append(Component.text(reconnectManager.isRunning() ? "運行中" : "已停止", 
                reconnectManager.isRunning() ? NamedTextColor.GREEN : NamedTextColor.RED))
            .append(Component.newline())
            .append(Component.text("重連嘗試次數: ", NamedTextColor.YELLOW))
            .append(Component.text(String.valueOf(reconnectManager.getReconnectAttempts()), NamedTextColor.WHITE))
            .append(Component.newline())
            .append(Component.text("Lobby服務器狀態: ", NamedTextColor.YELLOW))
            .append(Component.text(serverManager.isLobbyOnline() ? "在線" : "離線", 
                serverManager.isLobbyOnline() ? NamedTextColor.GREEN : NamedTextColor.RED))
            .build();
        
        sendMessage(invocation, status);
    }
    
    private void showServersStatus(Invocation invocation) {
        Component status = Component.text()
            .append(Component.text("=== 服務器狀態 ===", NamedTextColor.GOLD))
            .append(Component.newline())
            .append(Component.text("Lobby服務器: ", NamedTextColor.YELLOW))
            .append(Component.text(serverManager.isLobbyOnline() ? "在線" : "離線", 
                serverManager.isLobbyOnline() ? NamedTextColor.GREEN : NamedTextColor.RED))
            .append(Component.newline())
            .append(Component.text("Wait服務器: ", NamedTextColor.YELLOW))
            .append(Component.text(serverManager.isWaitOnline() ? "在線" : "離線", 
                serverManager.isWaitOnline() ? NamedTextColor.GREEN : NamedTextColor.RED))
            .build();
        
        sendMessage(invocation, status);
    }
    
    private void forceReconnect(Invocation invocation) {
        if (!reconnectManager.isRunning()) {
            sendMessage(invocation, Component.text("自動重連管理器未運行！", NamedTextColor.RED));
            return;
        }
        
        reconnectManager.forceReconnect();
        sendMessage(invocation, Component.text("已發起強制重連請求！", NamedTextColor.GREEN));
    }
    
    private void sendMessage(Invocation invocation, Component message) {
        if (invocation.source() instanceof Player) {
            ((Player) invocation.source()).sendMessage(message);
        } else {
            // 控制台輸出
            System.out.println(message.toString());
        }
    }
}
