# 監視要件書 (Monitoring Requirements)

## 概要
AWS CloudWatch を使用したアプリケーション監視とログ分析の要件を定義します。
Day 2 Phase 7 で実装する運用監視機能の詳細を記載しています。

---

## 1. 監視の目的

### 1.1 監視の重要性

- **可用性の確保**: システムが正常に稼働しているかを監視
- **パフォーマンス監視**: レスポンス時間、スループットの監視
- **異常検知**: エラーや障害の早期発見
- **容量計画**: リソース使用状況の把握と将来予測
- **コスト最適化**: 不要なリソースの特定

---

## 2. CloudWatch メトリクス監視

### 2.1 RDS PostgreSQL メトリクス

#### CPU 使用率

**メトリクス名**: `CPUUtilization`

**監視項目**:
- 現在の CPU 使用率
- 過去24時間の平均
- ピーク時の使用率

**アラーム設定**:
```yaml
CPUUtilization:
  Threshold: 80%
  EvaluationPeriods: 2
  Period: 300 (5分)
  Statistic: Average
  ComparisonOperator: GreaterThanThreshold
  AlarmActions:
    - SNS Topic: ecsite-alerts
```

**対応アクション**:
- 80%超過: 通知のみ
- 90%超過: インスタンスサイズの見直し検討

---

#### データベース接続数

**メトリクス名**: `DatabaseConnections`

**監視項目**:
- アクティブな接続数
- 最大接続数に対する使用率
- 接続数の推移

**アラーム設定**:
```yaml
DatabaseConnections:
  Threshold: 80 (max_connections の 80%)
  EvaluationPeriods: 1
  Period: 60 (1分)
  Statistic: Average
  ComparisonOperator: GreaterThanThreshold
  AlarmActions:
    - SNS Topic: ecsite-alerts
```

**対応アクション**:
- コネクションプールの設定確認
- コネクションリーク調査
- `max_connections` パラメータの見直し

---

#### メモリ使用量

**メトリクス名**: `FreeableMemory`

**監視項目**:
- 使用可能なメモリ量
- メモリ使用率の推移
- スワップ使用状況

**アラーム設定**:
```yaml
FreeableMemory:
  Threshold: 256 MB (256 * 1024 * 1024 bytes)
  EvaluationPeriods: 2
  Period: 300 (5分)
  Statistic: Average
  ComparisonOperator: LessThanThreshold
  AlarmActions:
    - SNS Topic: ecsite-alerts
```

**対応アクション**:
- クエリの最適化
- インスタンスサイズの見直し
- キャッシュ設定の確認

---

#### ストレージ容量

**メトリクス名**: `FreeStorageSpace`

**監視項目**:
- 使用可能なストレージ容量
- ストレージ使用率
- ストレージ増加傾向

**アラーム設定**:
```yaml
FreeStorageSpace:
  Threshold: 2 GB (2 * 1024 * 1024 * 1024 bytes)
  EvaluationPeriods: 1
  Period: 300 (5分)
  Statistic: Average
  ComparisonOperator: LessThanThreshold
  AlarmActions:
    - SNS Topic: ecsite-critical-alerts
```

**対応アクション**:
- 古いログの削除
- ストレージの拡張
- データのアーカイブ

---

#### 読み書きレイテンシ

**メトリクス名**: 
- `ReadLatency`
- `WriteLatency`

**監視項目**:
- 読み込み/書き込みの平均レイテンシ
- P99 レイテンシ
- レイテンシのスパイク

**アラーム設定**:
```yaml
ReadLatency:
  Threshold: 10 ms (0.01 seconds)
  EvaluationPeriods: 3
  Period: 60 (1分)
  Statistic: Average
  ComparisonOperator: GreaterThanThreshold
  
WriteLatency:
  Threshold: 20 ms (0.02 seconds)
  EvaluationPeriods: 3
  Period: 60 (1分)
  Statistic: Average
  ComparisonOperator: GreaterThanThreshold
```

---

#### IOPS (I/O Operations Per Second)

**メトリクス名**:
- `ReadIOPS`
- `WriteIOPS`

**監視項目**:
- 読み込み/書き込みの IOPS
- IOPS の使用率
- ストレージタイプの限界との比較

**アラーム設定**:
```yaml
ReadIOPS:
  Threshold: 2000 (gp3 の基本 IOPS の 66%)
  EvaluationPeriods: 2
  Period: 300 (5分)
  Statistic: Average
  ComparisonOperator: GreaterThanThreshold
```

---

### 2.2 EC2 メトリクス (アプリケーションサーバー)

#### CPU 使用率

**メトリクス名**: `CPUUtilization`

**アラーム設定**:
```yaml
CPUUtilization:
  Threshold: 70%
  EvaluationPeriods: 2
  Period: 300 (5分)
  Statistic: Average
```

#### ネットワークトラフィック

**メトリクス名**:
- `NetworkIn`
- `NetworkOut`

#### ディスク使用率

**メトリクス名**:
- `DiskReadBytes`
- `DiskWriteBytes`

---

## 3. CloudWatch Logs

### 3.1 ログの種類

#### RDS PostgreSQL ログ

**ログストリーム**:
- `/aws/rds/instance/{db-instance-id}/postgresql`
- `/aws/rds/instance/{db-instance-id}/error`

**ログ設定** (`postgresql.conf`):
```
log_statement = 'all'
log_min_duration_statement = 1000  # 1秒以上のクエリをログ出力
log_connections = on
log_disconnections = on
log_checkpoints = on
log_lock_waits = on
```

**監視項目**:
- スロークエリ (1秒以上)
- エラーログ
- 接続/切断ログ
- デッドロック

---

#### Spring Boot アプリケーションログ

**ログストリーム**:
- `/aws/ec2/ecsite-backend/application`
- `/aws/ec2/ecsite-backend/error`

**ログフォーマット** (`logback-spring.xml`):
```xml
<pattern>
    %d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n
</pattern>
```

**ログレベル**:
```properties
logging.level.root=INFO
logging.level.com.example.ecsite=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

**監視項目**:
- ERROR レベルのログ
- 例外スタックトレース
- API レスポンス時間
- ユーザーアクセスログ

---

### 3.2 ログインサイト (CloudWatch Logs Insights)

#### スロークエリの抽出

```sql
fields @timestamp, @message
| filter @message like /duration:/
| parse @message /duration: (?<duration>\d+\.\d+) ms/
| filter duration > 1000
| sort duration desc
| limit 20
```

#### エラーログの集計

```sql
fields @timestamp, @message
| filter @message like /ERROR/
| stats count() by bin(5m)
```

#### API エンドポイント別のレスポンス時間

```sql
fields @timestamp, method, uri, status, duration
| filter uri like /\/api\//
| stats avg(duration), max(duration), count() by uri
| sort avg(duration) desc
```

#### エラー率の計算

```sql
fields @timestamp, status
| filter status >= 400
| stats count() as error_count by bin(5m)
| stats sum(error_count) as total_errors
```

---

## 4. CloudWatch ダッシュボード

### 4.1 ダッシュボード構成

**ダッシュボード名**: `ECSite-Production-Dashboard`

#### ウィジェット構成

**Row 1: RDS パフォーマンス**
- CPU Utilization (折れ線グラフ)
- Database Connections (折れ線グラフ)
- Freeable Memory (折れ線グラフ)
- Free Storage Space (折れ線グラフ)

**Row 2: RDS レイテンシと IOPS**
- Read/Write Latency (折れ線グラフ)
- Read/Write IOPS (折れ線グラフ)

**Row 3: アプリケーション**
- EC2 CPU Utilization (折れ線グラフ)
- Network In/Out (折れ線グラフ)

**Row 4: エラーとアラーム**
- Recent Errors (ログウィジェット)
- Active Alarms (数値ウィジェット)

---

### 4.2 Terraform でのダッシュボード定義

```hcl
resource "aws_cloudwatch_dashboard" "ecsite_dashboard" {
  dashboard_name = "ECSite-Production-Dashboard"
  
  dashboard_body = jsonencode({
    widgets = [
      {
        type = "metric"
        properties = {
          metrics = [
            ["AWS/RDS", "CPUUtilization", {stat = "Average"}]
          ]
          period = 300
          stat = "Average"
          region = "ap-northeast-1"
          title = "RDS CPU Utilization"
        }
      },
      # その他のウィジェット定義...
    ]
  })
}
```

---

## 5. SNS アラーム通知

### 5.1 SNS トピック

**トピック名**: 
- `ecsite-alerts` - 通常のアラーム
- `ecsite-critical-alerts` - 重大なアラーム

**サブスクリプション**:
- Email: `devops-team@example.com`
- Slack Webhook (オプション)
- PagerDuty (本番環境)

---

### 5.2 Terraform での SNS 定義

```hcl
resource "aws_sns_topic" "ecsite_alerts" {
  name = "ecsite-alerts"
  
  tags = {
    Name = "ecsite-alerts"
    Environment = "production"
  }
}

resource "aws_sns_topic_subscription" "email_subscription" {
  topic_arn = aws_sns_topic.ecsite_alerts.arn
  protocol  = "email"
  endpoint  = var.alert_email
}

resource "aws_sns_topic_subscription" "slack_subscription" {
  topic_arn = aws_sns_topic.ecsite_alerts.arn
  protocol  = "https"
  endpoint  = var.slack_webhook_url
}
```

---

## 6. アラームアクション

### 6.1 アラーム発生時の対応フロー

```
┌─────────────────────┐
│ アラームトリガー     │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ SNS 通知送信         │
│ - Email              │
│ - Slack              │
│ - PagerDuty          │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ 担当者による確認     │
│ - CloudWatch 確認    │
│ - ログ確認           │
│ - メトリクス確認     │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ 対応アクション実行   │
│ - 設定変更           │
│ - スケールアップ     │
│ - 再起動             │
└─────────────────────┘
```

---

### 6.2 エスカレーションルール

| 重要度 | 初動対応時間 | エスカレーション |
|--------|-------------|-----------------|
| Critical | 15分以内 | 即座にオンコールエンジニア |
| High | 1時間以内 | 30分後にエスカレーション |
| Medium | 4時間以内 | 翌営業日対応 |
| Low | 24時間以内 | 週次レビューで対応 |

---

## 7. AI を活用したログ分析

### 7.1 CloudWatch Logs Insights と GitHub Copilot の連携

#### ログ分析プロンプト例

**プロンプト**:
```
@workspace /spec monitoring-requirements.md を読み込み、
以下のログデータから異常なパターンを検出してください:

[ログデータ]
2025-12-24 10:15:23.123 ERROR - Connection timeout to database
2025-12-24 10:15:24.456 ERROR - Connection timeout to database
2025-12-24 10:15:25.789 ERROR - Connection timeout to database
...
```

**期待される出力**:
- 接続タイムアウトの頻発
- データベース接続プールの枯渇の可能性
- 推奨対応: 接続プール設定の見直し

---

### 7.2 異常検知クエリの自動生成

**プロンプト**:
```
以下の要件に基づいて、CloudWatch Logs Insights のクエリを生成してください:
- 過去1時間のエラーログを抽出
- エンドポイント別にグループ化
- エラー発生回数が10回以上のエンドポイントのみ表示
```

**生成されるクエリ**:
```sql
fields @timestamp, uri, @message
| filter @message like /ERROR/
| filter @timestamp > ago(1h)
| stats count() as error_count by uri
| filter error_count >= 10
| sort error_count desc
```

---

## 8. パフォーマンスチューニング

### 8.1 スロークエリの最適化

**検出方法**:
```sql
fields @timestamp, @message
| filter @message like /duration:/
| parse @message /duration: (?<duration>\d+\.\d+) ms.*statement: (?<query>.*)/
| filter duration > 1000
| stats count() as slow_count, avg(duration) as avg_duration by query
| sort slow_count desc
```

**最適化アクション**:
1. インデックスの追加
2. クエリの書き換え
3. N+1問題の解消
4. キャッシュの導入

---

### 8.2 コネクションプールのチューニング

**現在の設定確認**:
```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
```

**監視項目**:
- アクティブな接続数
- アイドル接続数
- 待機時間

**推奨設定**:
```properties
# 本番環境
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=10
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

---

## 9. 定期レポート

### 9.1 日次レポート

**レポート内容**:
- 過去24時間のシステム稼働率
- エラー発生件数
- スロークエリトップ10
- リソース使用状況

**配信方法**:
- Email (毎朝9時)
- Slack (自動投稿)

---

### 9.2 週次レポート

**レポート内容**:
- 週間のパフォーマンストレンド
- アラーム発生状況
- コスト分析
- 改善提案

---

## 10. 実装チェックリスト

### Day 2 Phase 7 実装時

- [ ] RDS CloudWatch メトリクスの確認
- [ ] CloudWatch Logs の設定
- [ ] SNS トピックの作成
- [ ] アラームの作成
- [ ] CloudWatch ダッシュボードの作成
- [ ] ログインサイトクエリのテスト
- [ ] アラーム通知のテスト

### 運用開始前

- [ ] 全アラームの動作確認
- [ ] エスカレーションルールの確認
- [ ] オンコール体制の整備
- [ ] ログローテーションの設定
- [ ] 定期レポートの自動化

---

## 参考資料

- [Amazon CloudWatch Documentation](https://docs.aws.amazon.com/cloudwatch/)
- [CloudWatch Logs Insights クエリ構文](https://docs.aws.amazon.com/AmazonCloudWatch/latest/logs/CWL_QuerySyntax.html)
- [RDS Performance Insights](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/USER_PerfInsights.html)

---

## 関連ドキュメント

- [インフラ設計書](./infrastructure-design.md)
- [セキュリティ要件書](./security-requirements.md)
- [CI/CD タスク定義書](./tasks.md)
