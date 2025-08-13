# 項目結構說明

## 目錄結構

```
auto_dad/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── autodad/
│       │           └── autoreconnect/
│       │               ├── AutoReconnectPlugin.java    # 主插件類
│       │               ├── ServerManager.java          # 服務器管理器
│       │               ├── AutoReconnectManager.java   # 自動重連管理器
│       │               └── commands/
│       │                   └── ReconnectCommand.java   # 命令處理器
│       └── resources/
│           └── velocity-plugin.json                    # 插件配置文件
├── pom.xml                                              # Maven項目配置
├── README.md                                            # 項目說明文檔
├── config-example.toml                                  # Velocity配置示例
├── build.bat                                            # Windows編譯腳本
└── PROJECT_STRUCTURE.md                                 # 本文件
```

## 核心組件說明

### 1. AutoReconnectPlugin.java
- **功能**: 插件的主入口點
- **職責**: 
  - 初始化插件
  - 註冊命令
  - 管理插件生命週期

### 2. ServerManager.java
- **功能**: 管理lobby和wait服務器
- **職責**:
  - 註冊和監控服務器
  - 檢查服務器狀態
  - 提供服務器訪問接口

### 3. AutoReconnectManager.java
- **功能**: 實現wait服務器的自動重連
- **職責**:
  - 定期檢查服務器狀態
  - 自動重連離線的服務器
  - 管理重連策略和計數器

### 4. ReconnectCommand.java
- **功能**: 提供管理命令
- **職責**:
  - 處理玩家命令
  - 顯示插件狀態
  - 提供手動重連功能

## 編譯和安裝

### 方法1: 使用Maven（推薦）
```bash
mvn clean package
```

### 方法2: 使用提供的腳本
```bash
# Windows
build.bat

# Linux/Mac
./build.sh
```

### 安裝到Velocity
1. 將生成的JAR文件複製到Velocity的 `plugins/` 目錄
2. 在 `velocity.toml` 中配置lobby和wait服務器
3. 重啟Velocity代理

## 配置說明

### Velocity配置
在 `velocity.toml` 中添加：
```toml
[servers]
lobby = "127.0.0.1:25565"
wait = "127.0.0.1:25566"

[forced-hosts]
"lobby.example.com" = ["lobby"]
"wait.example.com" = ["wait"]
```

### 插件參數
可在 `AutoReconnectManager.java` 中修改：
- `CHECK_INTERVAL`: 檢查間隔（秒）
- `RECONNECT_DELAY`: 重連延遲（秒）
- `MAX_RECONNECT_ATTEMPTS`: 最大重連嘗試次數

## 使用命令

- `/reconnect status` - 顯示狀態
- `/reconnect reconnect force` - 強制重連
- `/reconnect servers` - 顯示服務器狀態
- `/reconnect help` - 顯示幫助

## 注意事項

1. 確保Java版本 >= 11
2. 確保Velocity版本 >= 3.3.0
3. 正確配置服務器地址和端口
4. 檢查防火牆和網絡設置
