# Auto Reconnect Plugin

一個Velocity代理插件，提供兩個分流服務器（lobby和wait）的管理，並實現wait服務器的自動重連功能。

## 功能特性

- **雙分流系統**: 管理lobby和wait兩個服務器
- **自動重連**: 當wait服務器離線時自動嘗試重連
- **智能監控**: 定期檢查服務器狀態
- **命令管理**: 提供完整的命令系統來管理插件
- **可配置**: 支持自定義重連參數

## 系統要求

- Java 11 或更高版本
- Velocity 3.3.0 或更高版本
- Maven 3.6 或更高版本

## 安裝說明

### 1. 編譯插件

```bash
mvn clean package
```

編譯完成後，JAR文件將位於 `target/` 目錄中。

### 2. 安裝到Velocity

1. 將編譯好的JAR文件複製到Velocity的 `plugins/` 目錄
2. 重啟Velocity代理
3. 確保在Velocity配置中已配置lobby和wait服務器

### 3. Velocity配置示例

在 `velocity.toml` 中添加服務器配置：

```toml
[servers]
lobby = "127.0.0.1:25565"
wait = "127.0.0.1:25566"

[forced-hosts]
"lobby.example.com" = ["lobby"]
"wait.example.com" = ["wait"]
```

## 使用方法

### 命令列表

- `/reconnect status` - 顯示自動重連狀態
- `/reconnect reconnect force` - 強制重連Wait服務器
- `/reconnect servers` - 顯示所有服務器狀態
- `/reconnect help` - 顯示幫助信息

### 別名

- `/recon` - `/reconnect` 的別名
- `/rc` - `/reconnect` 的別名

## 配置選項

插件支持以下配置參數（可在代碼中修改）：

- **檢查間隔**: 10秒（檢查服務器狀態的頻率）
- **重連延遲**: 5秒（重連嘗試之間的延遲）
- **最大重連嘗試次數**: 10次（達到後會重置計數器繼續嘗試）

## 工作原理

1. **服務器監控**: 插件每10秒檢查一次wait服務器的狀態
2. **自動檢測**: 當檢測到wait服務器離線時，自動啟動重連流程
3. **智能重連**: 使用延遲重連機制，避免過於頻繁的重連嘗試
4. **狀態恢復**: 當服務器恢復在線時，自動重置重連計數器

## 日誌信息

插件會記錄以下信息：

- 啟動和關閉信息
- 服務器狀態變化
- 重連嘗試和結果
- 錯誤和警告信息

## 故障排除

### 常見問題

1. **服務器無法連接**
   - 檢查服務器地址和端口配置
   - 確認服務器是否正在運行
   - 檢查防火牆設置

2. **重連失敗**
   - 檢查網絡連接
   - 確認服務器配置正確
   - 查看日誌中的錯誤信息

3. **插件無法啟動**
   - 確認Java版本符合要求
   - 檢查Velocity版本兼容性
   - 查看啟動日誌

## 開發信息

### 項目結構

```
src/main/java/com/autodad/autoreconnect/
├── AutoReconnectPlugin.java      # 主插件類
├── ServerManager.java            # 服務器管理器
├── AutoReconnectManager.java     # 自動重連管理器
└── commands/
    └── ReconnectCommand.java     # 命令處理器
```

### 構建

```bash
# 清理並編譯
mvn clean compile

# 運行測試
mvn test

# 打包
mvn package

# 安裝到本地倉庫
mvn install
```

## 許可證

此項目採用MIT許可證。

## 支持

如果您遇到問題或有建議，請：

1. 檢查日誌文件
2. 查看此README文件
3. 檢查Velocity官方文檔
4. 提交Issue或Pull Request

## 更新日誌

### v1.0.0
- 初始版本
- 支持lobby和wait雙分流
- 實現wait服務器自動重連
- 提供完整的命令系統
- 支持服務器狀態監控
