# インフラ設計書 (Infrastructure Design)

## 概要

EC サイトを AWS 上にデプロイするためのインフラ設計書です。

**重要**: この演習では Terraform 実行は行いません。設計レビューのみを実施します。

---

## 推奨デプロイ構成

### 1. EC2 + nginx（最も確実）✅ **推奨**

```
ALB (Public)
  ↓
EC2 (Public, t3.micro) x2
  ├─ nginx (Frontend: React 配信)
  └─ Spring Boot (Backend: port 8080)
  ↓
RDS PostgreSQL (Public, 学習用)
```

**理由**:

- ✅ 確実に動く（SSH でトラブルシューティング可能）
- ✅ Spring Boot は組み込み Tomcat で単体動作（アプリサーバー不要）
- ✅ nginx はリバースプロキシとして使用
- ✅ 受講生全員がゴールできる

**コスト見積もり**:

- EC2 (t3.micro) x2: 約 $15/月
- RDS (db.t3.micro): 約 $15/月
- ALB: 約 $20/月
- **合計**: 約 $50/月

---

### 2. S3 + CloudFront + EC2（コスト重視）

```
CloudFront → S3 (Frontend: React 静的)
ALB → EC2 (Backend: Spring Boot) → RDS PostgreSQL
```

**理由**:

- ✅ Frontend は確実に動く（静的配信）
- ✅ Backend だけ EC2 で管理
- ✅ コスト最安（約 $48/月）
- ❌ 2つの構成を作る必要がある

---

### 3. AWS Elastic Beanstalk（PaaS）💡 参考

```
Elastic Beanstalk
  ├─ Java Platform
  ├─ Spring Boot JAR をアップロード
  └─ 自動で ALB + EC2 + Auto Scaling 構築
  ↓
RDS PostgreSQL
```

**理由**:

- ✅ Azure App Service と同等
- ✅ `application.properties` に RDS 接続情報を書くだけ
- ❌ 学習曲線あり
- ❌ 実際には使われていない（コンテナ時代に取り残された）

---

## AWS アーキテクチャ（推奨構成: EC2 + nginx）

### システム構成図

```
┌─────────────────────────────────────────────────────────┐
│                    AWS Cloud (ap-northeast-1)            │
│                                                          │
│  ┌────────────────────────────────────────────────┐    │
│  │              VPC (10.0.0.0/16)                 │    │
│  │                                                 │    │
│  │  ┌──────────────────────────────────────────┐  │    │
│  │  │           ALB (Public)                   │  │    │
│  │  │         Listener: HTTP 80                │  │    │
│  │  └────────────┬─────────────────────────────┘  │    │
│  │               │                                 │    │
│  │  ┌────────────▼──────────┐  ┌──────────────┐  │    │
│  │  │ Public Subnet-1a      │  │ Public-1c    │  │    │
│  │  │  10.0.1.0/24          │  │ 10.0.2.0/24  │  │    │
│  │  │                       │  │              │  │    │
│  │  │ EC2 (t3.micro)        │  │ EC2          │  │    │
│  │  │  ├─ nginx (Frontend)  │  │  ├─ nginx   │  │    │
│  │  │  └─ Spring Boot :8080 │  │  └─ Spring  │  │    │
│  │  └────────┬──────────────┘  └──────┬───────┘  │    │
│  │           │                        │           │    │
│  │  ┌────────▼────────────────────────▼────────┐  │    │
│  │  │       Public Subnet (RDS 学習用)         │  │    │
│  │  │         10.0.10.0/24                     │  │    │
│  │  │                                          │  │    │
│  │  │        ┌─────────────────┐              │  │    │
│  │  │        │   RDS PostgreSQL │              │  │    │
│  │  │        │   14.20          │              │  │    │
│  │  │        │   db.t3.micro    │              │  │    │
│  │  │        │   Single-AZ      │              │  │    │
│  │  │        └─────────────────┘              │  │    │
│  │  └──────────────────────────────────────────┘  │    │
│  │                                                 │    │
│  └─────────────────────────────────────────────────┘    │
│                                                          │
│  ┌─────────────────────────────────────────────────┐    │
│  │           CloudWatch (監視・ログ)                │    │
│  │  - RDS メトリクス                               │    │
│  │  - EC2 ログ                                     │    │
│  │  - アラーム設定                                  │    │
│  └─────────────────────────────────────────────────┘    │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

---

## リソース仕様

### 1. VPC (Virtual Private Cloud)

| 項目          | 値                      |
| ------------- | ----------------------- |
| CIDR ブロック | `10.0.0.0/16`           |
| リージョン    | `ap-northeast-1` (東京) |
| DNS ホスト名  | 有効                    |
| DNS 解決      | 有効                    |

**タグ**:

```
Name: ecsite-vpc
Environment: training
Project: ai-driven-dev
```

---

### 2. サブネット

#### Public Subnet (アプリケーション層)

| 項目                     | Subnet-1a                 | Subnet-1c                 |
| ------------------------ | ------------------------- | ------------------------- |
| CIDR ブロック            | `10.0.1.0/24`             | `10.0.2.0/24`             |
| アベイラビリティゾーン   | `ap-northeast-1a`         | `ap-northeast-1c`         |
| パブリックIP自動割り当て | 有効                      | 有効                      |
| 用途                     | EC2 (nginx + Spring Boot) | EC2 (nginx + Spring Boot) |

#### Public Subnet (データベース層 - 学習用)

| 項目                     | 値                                           |
| ------------------------ | -------------------------------------------- |
| CIDR ブロック            | `10.0.10.0/24`                               |
| アベイラビリティゾーン   | `ap-northeast-1a`                            |
| パブリックIP自動割り当て | 有効                                         |
| 用途                     | RDS PostgreSQL（学習用、管理用アクセス可能） |

**⚠️ 本番環境での推奨**:

- RDS は Private Subnet に配置
- パブリックアクセス無効
- VPC 内部からのみアクセス可能

---

### 3. インターネットゲートウェイ

| 項目       | 値           |
| ---------- | ------------ |
| 名前       | `ecsite-igw` |
| アタッチ先 | `ecsite-vpc` |

---

### 4. ルートテーブル

#### Public Route Table

| 送信先        | ターゲット   |
| ------------- | ------------ |
| `10.0.0.0/16` | local        |
| `0.0.0.0/0`   | `ecsite-igw` |

**関連付け**:

- Public Subnet-1a
- Public Subnet-1c
- Public Subnet (RDS 用)

---

### 5. セキュリティグループ

#### アプリケーション用セキュリティグループ

**名前**: `ecsite-app-sg`

**インバウンドルール**:

| タイプ     | プロトコル | ポート範囲 | ソース         | 説明               |
| ---------- | ---------- | ---------- | -------------- | ------------------ |
| HTTP       | TCP        | 80         | `0.0.0.0/0`    | HTTP アクセス      |
| HTTPS      | TCP        | 443        | `0.0.0.0/0`    | HTTPS アクセス     |
| Custom TCP | TCP        | 8080       | `0.0.0.0/0`    | Spring Boot アプリ |
| SSH        | TCP        | 22         | `{Your IP}/32` | 管理用SSH          |

**アウトバウンドルール**:

- すべてのトラフィック許可

#### データベース用セキュリティグループ

**名前**: `ecsite-db-sg`

**インバウンドルール**:

| タイプ     | プロトコル | ポート範囲 | ソース          | 説明                 |
| ---------- | ---------- | ---------- | --------------- | -------------------- |
| PostgreSQL | TCP        | 5432       | `ecsite-app-sg` | アプリからのDB接続   |
| PostgreSQL | TCP        | 5432       | `{Your IP}/32`  | 管理用接続（学習用） |

**アウトバウンドルール**:

- すべてのトラフィック許可

---

### 6. RDS PostgreSQL

#### インスタンス仕様

| 項目                   | 値                                                   |
| ---------------------- | ---------------------------------------------------- |
| エンジン               | PostgreSQL **14.20** ⚠️ 14.11 は古くエラーになります |
| インスタンスクラス     | `db.t3.micro`                                        |
| vCPU                   | 2                                                    |
| メモリ                 | 1 GB                                                 |
| ストレージタイプ       | 汎用 SSD (gp3)                                       |
| ストレージサイズ       | 20 GB                                                |
| 自動バックアップ       | 有効 (7日間保持)                                     |
| Multi-AZ 配置          | **無効** (コスト削減のため Single-AZ)                |
| パブリックアクセス可能 | **有効**（学習用、本番環境では無効推奨）             |

#### 接続情報

| 項目               | 値                                                        |
| ------------------ | --------------------------------------------------------- |
| エンドポイント     | `{db-instance-id}.xxxxx.ap-northeast-1.rds.amazonaws.com` |
| ポート             | `5432`                                                    |
| データベース名     | `ecsite`                                                  |
| マスターユーザー名 | `postgres`                                                |
| マスターパスワード | `{環境変数で管理}`                                        |

#### DB サブネットグループ

| 項目       | 値                           |
| ---------- | ---------------------------- |
| 名前       | `ecsite-db-subnet-group`     |
| サブネット | Public Subnet (10.0.10.0/24) |

#### パラメータグループ

| 項目       | 値                         |
| ---------- | -------------------------- |
| 名前       | `ecsite-postgres14-params` |
| ファミリー | `postgres14`               |

**カスタムパラメータ**:

```
max_connections = 100
shared_buffers = 256MB
log_statement = 'all'
log_min_duration_statement = 1000
```

---

### 7. Application Load Balancer (ALB)

#### ALB 仕様

| 項目              | 値                                 |
| ----------------- | ---------------------------------- |
| 名前              | `ecsite-alb`                       |
| スキーム          | internet-facing                    |
| IP アドレスタイプ | ipv4                               |
| サブネット        | Public Subnet-1a, Public Subnet-1c |

#### ターゲットグループ

**名前**: `ecsite-app-tg`

| 項目               | 値          |
| ------------------ | ----------- |
| ターゲットタイプ   | instance    |
| プロトコル         | HTTP        |
| ポート             | 80          |
| VPC                | ecsite-vpc  |
| ヘルスチェックパス | `/` (nginx) |
| ヘルスチェック間隔 | 30秒        |

#### リスナー

| プロトコル | ポート | デフォルトアクション     |
| ---------- | ------ | ------------------------ |
| HTTP       | 80     | Forward to ecsite-app-tg |

---

### 8. EC2 インスタンス

#### インスタンス仕様

| 項目               | 値                                 |
| ------------------ | ---------------------------------- |
| AMI                | Amazon Linux 2023                  |
| インスタンスタイプ | `t3.micro`                         |
| vCPU               | 2                                  |
| メモリ             | 1 GB                               |
| 台数               | 2 (冗長構成)                       |
| 配置               | Public Subnet-1a, Public Subnet-1c |

#### インストールするソフトウェア

```bash
# nginx
sudo yum install -y nginx

# OpenJDK 21
sudo yum install -y java-21-amazon-corretto

# Spring Boot アプリ
# /opt/app/ecsite-backend.jar
```

#### nginx 設定

```nginx
server {
    listen 80;
    server_name _;

    # Frontend (React)
    location / {
        root /var/www/html;
        try_files $uri $uri/ /index.html;
    }

    # Backend API (Spring Boot)
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

---

### 9. CloudWatch

#### RDS メトリクス監視

**監視項目**:

- `CPUUtilization` - CPU使用率
- `DatabaseConnections` - アクティブな接続数
- `FreeableMemory` - 使用可能なメモリ
- `FreeStorageSpace` - 使用可能なストレージ
- `ReadLatency` / `WriteLatency` - 読み書きレイテンシ
- `ReadIOPS` / `WriteIOPS` - I/O 操作数

#### EC2 メトリクス監視

**監視項目**:

- `CPUUtilization` - CPU使用率
- `NetworkIn` / `NetworkOut` - ネットワークトラフィック
- `StatusCheckFailed` - ステータスチェック

#### ログストリーム

**ロググループ**:

- `/aws/rds/instance/{db-instance-id}/postgresql` - PostgreSQL ログ
- `/aws/ec2/{instance-id}/nginx` - nginx アクセスログ
- `/aws/ec2/{instance-id}/spring-boot` - Spring Boot アプリログ

#### アラーム設定

| アラーム名            | メトリクス          | 閾値             | アクション |
| --------------------- | ------------------- | ---------------- | ---------- |
| `high-cpu-usage-rds`  | RDS CPUUtilization  | 80% 超過 (5分間) | SNS 通知   |
| `high-db-connections` | DatabaseConnections | 80 接続超過      | SNS 通知   |
| `low-free-storage`    | FreeStorageSpace    | 2GB 未満         | SNS 通知   |
| `high-cpu-usage-ec2`  | EC2 CPUUtilization  | 80% 超過 (5分間) | SNS 通知   |

---

## コスト見積もり

### 月額コスト (概算)

#### 推奨構成: EC2 + nginx

| リソース       | スペック          | 月額 (USD) |
| -------------- | ----------------- | ---------- |
| EC2 (t3.micro) | x2                | ~$15       |
| RDS PostgreSQL | db.t3.micro, 20GB | ~$15       |
| ALB            | 標準料金          | ~$20       |
| VPC            | 標準料金          | 無料       |
| CloudWatch     | 基本メトリクス    | 無料       |
| データ転送     | 少量想定          | ~$5        |
| **合計**       |                   | **~$55**   |

#### コスト削減案: S3 + EC2

| リソース        | スペック          | 月額 (USD) |
| --------------- | ----------------- | ---------- |
| S3 + CloudFront | Frontend 配信     | ~$5        |
| EC2 (t3.micro)  | x1 (Backend のみ) | ~$8        |
| RDS PostgreSQL  | db.t3.micro, 20GB | ~$15       |
| ALB             | 標準料金          | ~$20       |
| **合計**        |                   | **~$48**   |

**注意事項**:

- 上記は概算です。実際の使用量により変動します
- トレーニング終了後は必ずリソースを削除してください
- `terraform destroy` でリソースを削除（約 $50/月 の課金を停止）

---

## 設計レビューのポイント

### 1. なぜ EC2 + nginx を推奨するのか？

| 項目                   | EC2 + nginx         | ECS Fargate                |
| ---------------------- | ------------------- | -------------------------- |
| 起動の確実性           | ✅ 確実             | ❌ Private Subnet で不安定 |
| トラブルシューティング | ✅ SSH で容易       | ❌ ログ確認が複雑          |
| 学習コスト             | ✅ 低い             | ❌ ECS/Docker の知識必要   |
| 本番環境での推奨度     | ⚠️ 中（メンテ必要） | ✅ 高（推奨）              |

### 2. なぜ RDS を Public Subnet に配置するのか？

| 項目                   | Public Subnet (学習用) | Private Subnet (本番推奨) |
| ---------------------- | ---------------------- | ------------------------- |
| 管理用アクセス         | ✅ psql で直接接続可能 | ❌ Bastion 経由が必要     |
| セキュリティ           | ⚠️ IP 制限必須         | ✅ VPC 内部のみ           |
| トラブルシューティング | ✅ 容易                | ❌ 複雑                   |

### 3. なぜ Multi-AZ を無効にするのか？

| 項目     | Single-AZ (学習用) | Multi-AZ (本番推奨)     |
| -------- | ------------------ | ----------------------- |
| コスト   | ✅ 約 $15/月       | ❌ 約 $30/月            |
| 高可用性 | ❌ 無し            | ✅ 自動フェイルオーバー |
| 用途     | 学習・検証環境     | 本番環境                |

### 4. Spring Boot にアプリサーバーは不要

```
❌ 誤解: Spring Boot = Tomcat が別途必要

✅ 正解: Spring Boot = 組み込み Tomcat 内蔵
        java -jar app.jar だけで起動可能
```

**nginx の役割**:

- Frontend (React) の静的ファイル配信
- Backend (Spring Boot) へのリバースプロキシ

---

## セキュリティ考慮事項

### 学習環境での妥協点

| 項目                   | 学習環境           | 本番環境       |
| ---------------------- | ------------------ | -------------- |
| RDS 配置               | Public Subnet      | Private Subnet |
| RDS パブリックアクセス | 有効               | 無効           |
| Security Group         | 自分の IP のみ許可 | VPC 内部のみ   |
| Multi-AZ               | 無効               | 有効           |

### 認証情報管理

- RDS パスワードは環境変数で管理
- `.gitignore` に `application.properties` を追加
- 本番環境では AWS Secrets Manager を使用

### 監査とログ

- CloudWatch Logs で全ての操作をログ記録
- RDS の `log_statement = 'all'` で全SQLをログ出力
- アラームによる異常検知

---

## トラブルシューティング

### RDS 接続エラー

1. **Security Group のインバウンドルール確認**

   ```bash
   aws ec2 describe-security-groups --group-ids sg-xxxxx
   ```

2. **自分の IP アドレス確認**

   ```bash
   curl ifconfig.me
   ```

3. **RDS エンドポイント確認**
   ```bash
   psql -h {endpoint} -U postgres -d ecsite
   ```

### EC2 SSH 接続エラー

1. **Security Group の SSH ルール確認**（ポート 22）
2. **Key Pair の権限確認**

   ```bash
   chmod 400 your-key.pem
   ```

3. **接続テスト**
   ```bash
   ssh -i your-key.pem ec2-user@{public-ip}
   ```

### nginx が起動しない

1. **設定ファイルの文法チェック**

   ```bash
   sudo nginx -t
   ```

2. **ログ確認**
   ```bash
   sudo tail -f /var/log/nginx/error.log
   ```

### Spring Boot が起動しない

1. **Java バージョン確認**

   ```bash
   java -version  # 21 以上
   ```

2. **アプリケーションログ確認**

   ```bash
   sudo journalctl -u spring-boot -f
   ```

3. **ポート 8080 が使用されているか確認**
   ```bash
   sudo lsof -i :8080
   ```

---

## 関連ドキュメント

- [AWS VPC ドキュメント](https://docs.aws.amazon.com/vpc/)
- [AWS RDS PostgreSQL ドキュメント](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/CHAP_PostgreSQL.html)
- [AWS EC2 ドキュメント](https://docs.aws.amazon.com/ec2/)
- [nginx ドキュメント](https://nginx.org/en/docs/)
- [Spring Boot ドキュメント](https://docs.spring.io/spring-boot/docs/current/reference/html/)

---

## まとめ

### 学習環境での推奨構成

```
✅ ALB → EC2 (nginx + Spring Boot) x2 → RDS PostgreSQL (Public)

理由:
- 確実に動く
- トラブルシューティングが容易
- コストが明確（約 $50/月）
- 受講生全員がゴールできる
```

### 本番環境への移行時の変更点

```
1. RDS を Private Subnet に移動
2. RDS パブリックアクセスを無効化
3. Multi-AZ を有効化
4. Bastion サーバーを追加
5. WAF を追加
6. CloudFront を追加
7. Route 53 でドメイン管理
```

---

🎉 **インフラ設計完了です！**
