# Auto Reconnect Plugin

一個 Velocity 代理插件，提供大廳和等待伺服器功能，以及自動重連機制。

## 功能特性

- 🏠 **大廳伺服器管理**: 自動管理大廳伺服器狀態
- ⏳ **等待伺服器分流**: 當大廳伺服器不可用時，將玩家分流到等待伺服器
- 🔄 **自動重連**: 持續監控並嘗試重連大廳伺服器
- ⚙️ **可配置設定**: 支援自定義伺服器名稱、通知訊息和重連參數
- 📝 **管理命令**: 提供重連和配置重載命令

## 安裝說明

1. 下載最新的 JAR 文件
2. 將 JAR 文件放入 Velocity 的 `plugins` 資料夾
3. 重啟 Velocity 代理
4. 插件會自動創建配置文件

## 配置文件

插件會在 `plugins/auto-reconnect/` 資料夾中創建 `config.yml` 配置文件。

### 配置選項

#### 伺服器設定
```yaml
servers:
  # 大廳伺服器名稱
  lobby: "lobby"
  # 等待伺服器名稱
  wait: "wait"
```

#### 通知設定
```yaml
notifications:
  # 是否啟用通知
  enabled: true
  # 通知前綴 (支援 Minecraft 顏色代碼)
  prefix: "&6[AutoReconnect] &r"
  # 重連嘗試訊息
  reconnect_message: "正在嘗試重新連接到伺服器..."
  # 成功重連訊息
  success_message: "成功重新連接到伺服器！"
  # 重連失敗訊息
  failed_message: "無法連接到伺服器，請稍後再試。"
  # 等待伺服器可用訊息
  wait_message: "正在等待伺服器可用..."
```

#### 重連設定
```yaml
reconnect:
  # 最大重連嘗試次數
  max_attempts: 5
  # 重連間隔時間 (秒)
  delay_seconds: 3
  # 檢查伺服器狀態間隔 (秒)
  check_interval_seconds: 10
```

## 命令

### 玩家命令
- `/reconnect` (別名: `/recon`, `/rc`) - 手動嘗試重連到大廳伺服器

### 管理員命令
- `/autoreload` (別名: `/ar`, `/reload`) - 重新載入配置文件
  - 權限: `autoreconnect.reload`

## 權限節點

- `autoreconnect.reload` - 允許重載配置文件

## 工作原理

1. **啟動時**: 插件會自動檢測並註冊大廳和等待伺服器
2. **監控**: 定期檢查大廳伺服器狀態
3. **分流**: 當大廳伺服器不可用時，新玩家會被分流到等待伺服器
4. **重連**: 持續嘗試重連大廳伺服器
5. **轉移**: 當大廳伺服器恢復時，自動將等待伺服器的玩家轉移過去

## 開發者資訊

- **Java 版本**: 11+
- **Velocity 版本**: 3.4.0-SNAPSHOT+
- **依賴**: SnakeYAML 2.0

## 故障排除

### 常見問題

1. **插件無法啟動**
   - 檢查 Velocity 版本是否支援
   - 檢查 Java 版本是否為 11 或更高

2. **配置文件錯誤**
   - 刪除 `config.yml` 文件，讓插件重新生成
   - 檢查 YAML 語法是否正確

3. **伺服器無法連接**
   - 確認伺服器名稱在 Velocity 配置中正確設定
   - 檢查伺服器是否正在運行

### 日誌

插件會記錄詳細的日誌信息到 Velocity 的日誌文件中。啟用 debug 模式可以獲得更多信息。

## 支援

如果您遇到問題或有建議，請在 GitHub 上創建 issue。

## 授權

本項目採用 MIT 授權條款。
