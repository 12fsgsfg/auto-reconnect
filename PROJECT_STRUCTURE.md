# 項目結構說明

## 目錄結構

```
auto-reconnect/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── autodad/
│       │           └── autoreconnect/
│       │               ├── AutoReconnectPlugin.java      # 主插件類
│       │               ├── ConfigManager.java            # 配置管理器
│       │               ├── ServerManager.java            # 伺服器管理器
│       │               ├── AutoReconnectManager.java     # 自動重連管理器
│       │               └── commands/                     # 命令包
│       │                   ├── ReconnectCommand.java     # 重連命令
│       │                   └── ReloadConfigCommand.java  # 重載配置命令
│       └── resources/
│           └── velocity-plugin.json                      # Velocity 插件描述文件
├── target/                                               # 編譯輸出目錄
├── config.yml                                            # 示例配置文件
├── pom.xml                                               # Maven 項目配置
├── README.md                                             # 英文說明文檔
├── 使用說明.md                                           # 中文使用說明
├── PROJECT_STRUCTURE.md                                  # 項目結構說明
└── LICENSE                                               # 授權文件
```

## 核心類說明

### 1. AutoReconnectPlugin.java
**主插件類**
- 負責插件的啟動和關閉
- 初始化各個管理器
- 註冊命令
- 管理插件生命週期

**主要方法:**
- `onProxyInitialization()` - 插件啟動時執行
- `onProxyShutdown()` - 插件關閉時執行
- `registerCommands()` - 註冊命令

### 2. ConfigManager.java
**配置管理器**
- 管理 `config.yml` 配置文件
- 提供配置值的讀取方法
- 支援配置熱重載
- 自動創建默認配置

**主要功能:**
- 伺服器名稱配置
- 通知訊息配置
- 重連參數配置
- 配置驗證和錯誤處理

### 3. ServerManager.java
**伺服器管理器**
- 管理大廳和等待伺服器
- 監控伺服器狀態
- 處理伺服器註冊和刷新
- 提供伺服器狀態查詢

**主要方法:**
- `initialize()` - 初始化伺服器管理器
- `isLobbyOnline()` - 檢查大廳伺服器狀態
- `isWaitOnline()` - 檢查等待伺服器狀態
- `refreshAllServers()` - 刷新所有伺服器

### 4. AutoReconnectManager.java
**自動重連管理器**
- 實現自動重連邏輯
- 管理重連嘗試次數
- 處理玩家轉移
- 發送通知訊息

**主要功能:**
- 定期檢查伺服器狀態
- 自動重連嘗試
- 玩家分流管理
- 重連狀態監控

### 5. 命令類

#### ReconnectCommand.java
**重連命令**
- 允許玩家手動重連
- 檢查權限
- 提供命令別名

#### ReloadConfigCommand.java
**重載配置命令**
- 重新載入配置文件
- 僅限管理員使用
- 支援控制台執行

## 配置文件結構

### config.yml
```yaml
# 伺服器設定
servers:
  lobby: "lobby"    # 大廳伺服器名稱
  wait: "wait"      # 等待伺服器名稱

# 通知設定
notifications:
  enabled: true     # 是否啟用通知
  prefix: "&6[AutoReconnect] &r"  # 通知前綴
  # ... 其他通知設定

# 重連設定
reconnect:
  max_attempts: 5           # 最大重連嘗試次數
  delay_seconds: 3          # 重連間隔時間
  check_interval_seconds: 10 # 檢查間隔時間
```

## 依賴關係

```
AutoReconnectPlugin
├── ConfigManager
├── ServerManager (依賴 ConfigManager)
├── AutoReconnectManager (依賴 ServerManager, ConfigManager)
└── Commands (依賴各個管理器)
```

## 插件生命週期

1. **啟動階段**
   - 創建 ConfigManager
   - 創建 ServerManager
   - 創建 AutoReconnectManager
   - 註冊命令
   - 啟動自動重連

2. **運行階段**
   - 定期檢查伺服器狀態
   - 處理玩家連接和轉移
   - 執行自動重連邏輯
   - 響應命令請求

3. **關閉階段**
   - 停止自動重連
   - 清理資源
   - 關閉調度器

## 擴展點

### 添加新伺服器類型
1. 在 `ConfigManager` 中添加新的配置項
2. 在 `ServerManager` 中添加相應的管理方法
3. 更新配置文件結構

### 自定義通知系統
1. 擴展 `ConfigManager` 的通知配置
2. 在 `AutoReconnectManager` 中實現自定義通知邏輯
3. 支援多語言和自定義格式

### 添加新的重連策略
1. 在 `ConfigManager` 中添加策略配置
2. 在 `AutoReconnectManager` 中實現策略邏輯
3. 支援動態策略切換

## 開發指南

### 編譯項目
```bash
mvn clean package
```

### 運行測試
```bash
mvn test
```

### 安裝到本地倉庫
```bash
mvn install
```

### 開發環境設置
1. 確保 Java 11+ 已安裝
2. 安裝 Maven 3.6+
3. 配置 IDE 支持 Maven 項目
4. 導入項目依賴

## 注意事項

- 所有配置值都通過 `ConfigManager` 獲取
- 伺服器名稱必須與 Velocity 配置一致
- 配置文件修改後需要使用 `/autoreload` 命令重載
- 插件依賴 SnakeYAML 處理配置文件
- 支援熱重載，無需重啟伺服器
