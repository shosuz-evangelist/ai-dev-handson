# Day2: インフラ設計（Spec Kit活用）

## 🎯 演習の目標
Spec Kit を使って、AWS インフラ設計書を生成し、**実装せずに**設計レビューできる状態を作る。

**重要**: この演習では **Terraform実行は行いません**。講師側でRDS接続を確保済みです。

---

## 📋 前提条件
- Day2: Backend実装が完了している
- Day2: Frontend実装が完了している
- GitHub Copilot Chat が有効
- Spec Kit がインストール済み
- **Terraform はインストール不要**（実行しないため）

---

## 🚀 Phase 3: Spec Kit で仕様書生成

### ステップ1: Copilot Chat を開く
VS Code で **Ctrl+Shift+I**（または Cmd+Shift+I）を押して、GitHub Copilot Chat を開く。

---

### ステップ2: 以下の内容を Copilot Chat に貼り付け

**重要**: 以下の内容を **そのままコピー**して Copilot Chat に貼り付けてください。

（↓ この部分をコピー ↓）

```
# Day2 EC Infrastructure Requirements

[day2-ec-infra]
title = "Day2 EC: インフラ設計（AWS EC2 + nginx + RDS PostgreSQL）"
generate = [
  { path = ".github/spec/generated/infrastructure-design.md", kind = "infrastructure-design" }
]

constraints = [
  "Source of truth is .github/spec/*.md",
  "Security-first: Use VPC, Security Groups, IAM Roles",
  "Cost-optimization: Use t3.micro for learning environment",
  "Monitoring: Enable CloudWatch Logs and Metrics",
  "Deployment: EC2 + nginx (most reliable for training)"
]

## 推奨デプロイ構成

### 1. EC2 + nginx（最も確実）✅ 推奨
構成:
- ALB (Public Subnet)
- EC2 (t3.micro) x2 (Public Subnet)
  - nginx: Frontend (React) 配信
  - Spring Boot: Backend (port 8080)
- RDS PostgreSQL (Public Subnet, 学習用)

理由:
- 確実に動く（SSH でトラブルシューティング可能）
- Spring Boot は組み込み Tomcat で単体動作（アプリサーバー不要）
- nginx はリバースプロキシとして使用
- 受講生全員がゴールできる

コスト: 約 $50/月

### 2. S3 + CloudFront + EC2（コスト重視）
構成:
- CloudFront → S3 (Frontend: React 静的)
- ALB → EC2 (Backend: Spring Boot) → RDS PostgreSQL

理由:
- Frontend は確実に動く（静的配信）
- Backend だけ EC2 で管理
- コスト最安（約 $48/月）

### 3. AWS Elastic Beanstalk（PaaS）💡 参考
構成:
- Elastic Beanstalk (Java Platform)
- Spring Boot JAR をアップロード
- 自動で ALB + EC2 + Auto Scaling 構築
- RDS PostgreSQL

理由:
- Azure App Service と同等
- 学習曲線あり
- 実際には使われていない（コンテナ時代に取り残された）

## インフラ構成（推奨: EC2 + nginx）

### AWS VPC
- VPC: 10.0.0.0/16
- Public Subnets: 2 (10.0.1.0/24, 10.0.2.0/24) - EC2 用
- Public Subnet: 1 (10.0.10.0/24) - RDS 用（学習環境）
- Internet Gateway: あり

### AWS Application Load Balancer (ALB)
- スキーム: internet-facing
- Listener: HTTP 80
- Target Group: ecsite-app-tg (port 80)
- Health Check: GET / (nginx)
- 配置: Public Subnets (1a, 1c)

### AWS EC2
- AMI: Amazon Linux 2023
- Instance Type: t3.micro
- 台数: 2 (冗長構成)
- 配置: Public Subnet-1a, Public Subnet-1c
- ソフトウェア:
  - nginx: Frontend (React) 配信 + Backend へのリバースプロキシ
  - OpenJDK 21: Spring Boot 実行
  - Spring Boot アプリ: /opt/app/ecsite-backend.jar

### AWS RDS PostgreSQL
- Engine: postgres
- Engine Version: 14.20（⚠️ 14.11 は古くエラーになります）
- Instance: db.t3.micro
- Database: ecsite
- Multi-AZ: disabled（コスト削減のため Single-AZ）
- Backup: 7 days retention
- 配置: Public Subnet (10.0.10.0/24) - 学習用
- パブリックアクセス: 有効（学習用、本番環境では無効推奨）
- Security Group: Inbound 5432 from EC2 Security Group + 自分の IP

### Security Groups

#### アプリケーション用 (ecsite-app-sg)
Inbound:
- HTTP (80) from 0.0.0.0/0
- HTTPS (443) from 0.0.0.0/0
- TCP (8080) from 0.0.0.0/0 - Spring Boot
- SSH (22) from {Your IP}/32

#### データベース用 (ecsite-db-sg)
Inbound:
- PostgreSQL (5432) from ecsite-app-sg
- PostgreSQL (5432) from {Your IP}/32 - 管理用接続（学習用）

### AWS CloudWatch
- Log Groups:
  - /aws/rds/instance/{db-instance-id}/postgresql
  - /aws/ec2/{instance-id}/nginx
  - /aws/ec2/{instance-id}/spring-boot
- Metrics: CPU, Memory, Request Count, DB Connections
- Alarms:
  - RDS CPU > 80%
  - RDS Connections > 80
  - RDS Storage < 2GB
  - EC2 CPU > 80%

## コスト見積もり

| リソース | スペック | 月額 (USD) |
|---------|----------|-----------|
| EC2 (t3.micro) | x2 | ~$15 |
| RDS PostgreSQL | db.t3.micro, 20GB | ~$15 |
| ALB | 標準料金 | ~$20 |
| VPC | 標準料金 | 無料 |
| CloudWatch | 基本メトリクス | 無料 |
| データ転送 | 少量想定 | ~$5 |
| **合計** | | **~$55** |

## 設計のポイント

### なぜ EC2 + nginx を推奨するのか？
- ECS Fargate の問題: Private Subnet 配置時に起動が不安定
- EC2 のメリット: SSH でトラブルシューティングが容易
- Spring Boot の特徴: 組み込み Tomcat で単体動作可能（アプリサーバー不要）

### なぜ RDS を Public Subnet に配置するのか？
- 学習環境: psql で直接接続可能、トラブルシューティングが容易
- 本番環境: Private Subnet に配置、VPC 内部からのみアクセス

### なぜ Multi-AZ を無効にするのか？
- 学習環境: コスト削減のため（約 $15/月 節約）
- 本番環境: 高可用性のため Multi-AZ を有効にする

### デモ後の削除
- 重要: terraform destroy でリソースを削除する
- 理由: 放置すると課金が続く（約 $50/月）
```

---

### ステップ3: /speckit.specify を実行

Copilot Chat に以下を入力：

```
/speckit.specify
```

**期待される出力**: `.github/spec/generated/` 配下に以下のファイルが生成される

1. `infrastructure-design.md`

---

### ステップ4: 生成されたファイルを確認

```bash
ls -la .github/spec/generated/
```

✅ `infrastructure-design.md` が存在することを確認してください。

---

### ステップ5: reference/ と generated/ を比較（重要）

講師が事前に用意した **完成版** `.github/spec/reference/infrastructure-design.md` と、受講生が生成した `.github/spec/generated/infrastructure-design.md` を比較します。

#### 比較方法
```bash
diff .github/spec/reference/infrastructure-design.md .github/spec/generated/infrastructure-design.md
```

#### 💡 期待される結果
- **基本構成は同じ**（VPC, EC2, RDS, ALB, CloudWatch など）
- **推奨構成が一致**（EC2 + nginx）
- **PostgreSQL バージョンが 14.20**
- **コスト見積もりが含まれている**

#### 確認ポイント
- [ ] VPC構成（Public Subnets）が一致しているか？
- [ ] 推奨構成が「EC2 + nginx」になっているか？
- [ ] RDS の構成（Instance Type: db.t3.micro, Engine Version: 14.20, Multi-AZ: 無効）が一致しているか？
- [ ] Security Group の設定が一致しているか？
- [ ] CloudWatch の設定が含まれているか？
- [ ] コスト見積もりが含まれているか？

#### ⚠️ もし Spec Kit が詰まったら
`.github/spec/reference/infrastructure-design.md` を「正」として、設計レビューを進めてください。

---

## 🚀 設計レビュー（60分の読み解き演習）

### ステップ1: infrastructure-design.md を開く

```bash
cat .github/spec/generated/infrastructure-design.md
```

または

```bash
cat .github/spec/reference/infrastructure-design.md
```

---

### ステップ2: レビュー項目

#### 1. 推奨デプロイ構成
- [ ] 推奨構成が「EC2 + nginx」になっているか？
- [ ] 理由が明確に記載されているか？（確実に動く、SSH で管理可能、など）
- [ ] コスト見積もりが含まれているか？（約 $50/月）

#### 2. VPC 構成
- [ ] Public Subnets に ALB が配置されているか？
- [ ] Public Subnets に EC2 が配置されているか？
- [ ] Public Subnet に RDS が配置されているか？（学習用）
- [ ] Internet Gateway が配置されているか？

#### 3. EC2 構成
- [ ] Instance Type が `t3.micro` か？
- [ ] 台数が 2 台（冗長構成）か？
- [ ] nginx + Spring Boot がインストールされているか？
- [ ] nginx の役割が明確か？（Frontend 配信 + リバースプロキシ）

#### 4. RDS 構成
- [ ] Engine Version が `14.20` か？ ⚠️ 14.11 は古くエラーになります
- [ ] Instance Type が `db.t3.micro` か？
- [ ] Multi-AZ が無効になっているか？（学習環境）
- [ ] Backup Retention が 7日間か？
- [ ] パブリックアクセスが有効になっているか？（学習用）
- [ ] Security Group が EC2 + 自分の IP からの 5432 を許可しているか？

#### 5. ALB 構成
- [ ] Target Group が EC2 に紐付いているか？
- [ ] Health Check が `/` (nginx) を使用しているか？
- [ ] Listener が HTTP 80 を受け付けているか？

#### 6. CloudWatch 構成
- [ ] Log Groups が RDS, EC2, nginx, Spring Boot を含んでいるか？
- [ ] Metrics が CPU, Memory, Request Count, DB Connections を含んでいるか？
- [ ] Alarms が CPU > 80%, Connections > 80, Storage < 2GB で設定されているか？

#### 7. コスト見積もり
- [ ] EC2, RDS, ALB の月額コストが記載されているか？
- [ ] 合計が約 $50〜$55/月 になっているか？
- [ ] デモ後の削除方法（terraform destroy）が記載されているか？

---

## 📚 設計レビュー後の議論

### 議論ポイント1: PostgreSQL バージョン
- **推奨**: 14.20
- **非推奨**: 14.11（古くエラーになる）

### 議論ポイント2: なぜ EC2 + nginx を推奨するのか？
- **ECS Fargate の問題**: Private Subnet 配置時に起動が不安定
- **EC2 のメリット**: SSH でトラブルシューティングが容易
- **Spring Boot の特徴**: 組み込み Tomcat で単体動作可能（アプリサーバー不要）
- **nginx の役割**: Frontend 配信 + Backend へのリバースプロキシ

### 議論ポイント3: なぜ RDS を Public Subnet に配置するのか？
- **学習環境**: psql で直接接続可能、トラブルシューティングが容易
- **本番環境**: Private Subnet に配置、VPC 内部からのみアクセス

### 議論ポイント4: Multi-AZ を無効にする理由
- **学習環境**: コスト削減のため（約 $15/月 節約）
- **本番環境**: 高可用性のため Multi-AZ を有効にする

### 議論ポイント5: Spring Boot にアプリサーバーは不要
```
❌ 誤解: Spring Boot = Tomcat が別途必要

✅ 正解: Spring Boot = 組み込み Tomcat 内蔵
        java -jar app.jar だけで起動可能
```

### 議論ポイント6: nginx の役割
- Frontend (React) の静的ファイル配信
- Backend (Spring Boot) へのリバースプロキシ
- ロードバランシング（ALB がある場合は不要だが、単体でも機能）

### 議論ポイント7: デモ後の削除
- **重要**: `terraform destroy` でリソースを削除する
- **理由**: 放置すると課金が続く（約 $50/月）

### 議論ポイント8: 本番環境への移行時の変更点
1. RDS を Private Subnet に移動
2. RDS パブリックアクセスを無効化
3. Multi-AZ を有効化
4. Bastion サーバーを追加
5. WAF を追加
6. CloudFront を追加
7. Route 53 でドメイン管理

---

## ❌ トラブルシューティング

### エラー1: `@workspace が効かない`
**原因**: .md ファイルがエディタで開かれていない

**解決策**:
1. `.github/spec/generated/infrastructure-design.md` をエディタで開く
2. Copilot Chat を再度開く

### エラー2: Spec Kit が生成しない
**原因**: プロンプトが不明確

**解決策**:
- ステップ2 の内容を **そのままコピー** して貼り付ける
- `/speckit.specify` を実行

### エラー3: 生成された内容が reference と異なる
**原因**: Spec Kit の AI による解釈の違い

**解決策**:
- `.github/spec/reference/infrastructure-design.md` を「正」として、設計レビューを進める
- 基本構成（VPC, EC2, RDS, ALB, CloudWatch）が一致していれば OK

---

## 📚 参考資料
- `.github/spec/reference/infrastructure-design.md`（完成版）
- [AWS VPC ドキュメント](https://docs.aws.amazon.com/vpc/)
- [AWS EC2 ドキュメント](https://docs.aws.amazon.com/ec2/)
- [AWS RDS PostgreSQL ドキュメント](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/CHAP_PostgreSQL.html)
- [nginx ドキュメント](https://nginx.org/en/docs/)

---

## ✅ 次のステップ
インフラ設計が完了したら、以下に進みます：

1. ~~Day2: Backend実装~~ ✅ 完了
2. ~~Day2: Frontend実装~~ ✅ 完了
3. ~~Day2: インフラ設計~~ ✅ 完了
4. **演習4: CI/CD (GitHub Actions)** ← 次はこれ

---

🎉 **インフラ設計完了です！**
