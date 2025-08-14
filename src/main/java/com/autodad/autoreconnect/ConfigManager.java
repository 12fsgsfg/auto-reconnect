package com.autodad.autoreconnect;

import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    
    private final Logger logger;
    private final Path dataDirectory;
    private final File configFile;
    private Map<String, Object> config;
    
    public ConfigManager(Logger logger, Path dataDirectory) {
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.configFile = dataDirectory.resolve("config.yml").toFile();
        this.config = new HashMap<>();
        loadConfig();
    }
    
    @SuppressWarnings("unchecked")
    private void loadConfig() {
        try {
            if (!configFile.exists()) {
                createDefaultConfig();
            }
            
            try (InputStream input = new FileInputStream(configFile)) {
                Yaml yaml = new Yaml();
                config = yaml.load(input);
                if (config == null) {
                    config = new HashMap<>();
                }
            }
            
            logger.info("配置文件載入成功");
        } catch (IOException e) {
            logger.error("載入配置文件時發生錯誤: {}", e.getMessage());
            createDefaultConfig();
        }
    }
    
    private void createDefaultConfig() {
        try {
            if (!dataDirectory.toFile().exists()) {
                Files.createDirectories(dataDirectory);
            }
            
            config = getDefaultConfig();
            saveConfig();
            logger.info("已創建默認配置文件");
        } catch (IOException e) {
            logger.error("創建默認配置文件時發生錯誤: {}", e.getMessage());
        }
    }
    
    private Map<String, Object> getDefaultConfig() {
        Map<String, Object> defaultConfig = new HashMap<>();
        
        // 伺服器設定
        Map<String, Object> servers = new HashMap<>();
        servers.put("lobby", "lobby");
        servers.put("wait", "wait");
        defaultConfig.put("servers", servers);
        
        // 通知設定
        Map<String, Object> notifications = new HashMap<>();
        notifications.put("enabled", true);
        notifications.put("prefix", "&6[AutoReconnect] &r");
        notifications.put("reconnect_message", "正在嘗試重新連接到伺服器...");
        notifications.put("success_message", "成功重新連接到伺服器！");
        notifications.put("failed_message", "無法連接到伺服器，請稍後再試。");
        notifications.put("wait_message", "正在等待伺服器可用...");
        defaultConfig.put("notifications", notifications);
        
        // 重連設定
        Map<String, Object> reconnect = new HashMap<>();
        reconnect.put("max_attempts", 5);
        reconnect.put("delay_seconds", 3);
        reconnect.put("check_interval_seconds", 10);
        defaultConfig.put("reconnect", reconnect);
        
        return defaultConfig;
    }
    
    private void saveConfig() throws IOException {
        try (FileWriter writer = new FileWriter(configFile)) {
            Yaml yaml = new Yaml();
            yaml.dump(config, writer);
        }
    }
    
    public void reloadConfig() {
        loadConfig();
    }
    
    // 獲取伺服器名稱
    public String getLobbyServerName() {
        return getString("servers.lobby", "lobby");
    }
    
    public String getWaitServerName() {
        return getString("servers.wait", "wait");
    }
    
    // 獲取通知設定
    public boolean isNotificationsEnabled() {
        return getBoolean("notifications.enabled", true);
    }
    
    public String getNotificationPrefix() {
        return getString("notifications.prefix", "&6[AutoReconnect] &r");
    }
    
    public String getReconnectMessage() {
        return getString("notifications.reconnect_message", "正在嘗試重新連接到伺服器...");
    }
    
    public String getSuccessMessage() {
        return getString("notifications.success_message", "成功重新連接到伺服器！");
    }
    
    public String getFailedMessage() {
        return getString("notifications.failed_message", "無法連接到伺服器，請稍後再試。");
    }
    
    public String getWaitMessage() {
        return getString("notifications.wait_message", "正在等待伺服器可用...");
    }
    
    // 獲取重連設定
    public int getMaxAttempts() {
        return getInt("reconnect.max_attempts", 5);
    }
    
    public int getDelaySeconds() {
        return getInt("reconnect.delay_seconds", 3);
    }
    
    public int getCheckIntervalSeconds() {
        return getInt("reconnect.check_interval_seconds", 10);
    }
    
    // 輔助方法
    @SuppressWarnings("unchecked")
    private String getString(String path, String defaultValue) {
        try {
            String[] keys = path.split("\\.");
            Map<String, Object> current = config;
            
            for (int i = 0; i < keys.length - 1; i++) {
                Object value = current.get(keys[i]);
                if (value instanceof Map) {
                    current = (Map<String, Object>) value;
                } else {
                    return defaultValue;
                }
            }
            
            Object value = current.get(keys[keys.length - 1]);
            return value != null ? value.toString() : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    @SuppressWarnings("unchecked")
    private boolean getBoolean(String path, boolean defaultValue) {
        try {
            String[] keys = path.split("\\.");
            Map<String, Object> current = config;
            
            for (int i = 0; i < keys.length - 1; i++) {
                Object value = current.get(keys[i]);
                if (value instanceof Map) {
                    current = (Map<String, Object>) value;
                } else {
                    return defaultValue;
                }
            }
            
            Object value = current.get(keys[keys.length - 1]);
            if (value instanceof Boolean) {
                return (Boolean) value;
            } else if (value instanceof String) {
                return Boolean.parseBoolean((String) value);
            }
            return defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    @SuppressWarnings("unchecked")
    private int getInt(String path, int defaultValue) {
        try {
            String[] keys = path.split("\\.");
            Map<String, Object> current = config;
            
            for (int i = 0; i < keys.length - 1; i++) {
                Object value = current.get(keys[i]);
                if (value instanceof Map) {
                    current = (Map<String, Object>) value;
                } else {
                    return defaultValue;
                }
            }
            
            Object value = current.get(keys[keys.length - 1]);
            if (value instanceof Integer) {
                return (Integer) value;
            } else if (value instanceof String) {
                return Integer.parseInt((String) value);
            }
            return defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
