# CI/CD タスク定義書 (GitHub Actions Tasks)

## 概要
GitHub Actions を使用した CI/CD パイプラインの実装タスクを定義します。
Day 2 Phase 6 で実装する自動化タスクの詳細を記載しています。

---

## 1. CI/CD パイプライン概要

### 1.1 パイプラインの目的

- コードの品質保証
- 自動テストの実行
- ビルドの自動化
- デプロイの自動化
- インフラのプロビジョニング

### 1.2 トリガー条件

| イベント | ブランチ | 実行内容 |
|---------|---------|---------|
| `push` | `main` | ビルド + テスト + デプロイ |
| `push` | `develop` | ビルド + テスト |
| `pull_request` | `main`, `develop` | ビルド + テスト |
| `workflow_dispatch` | 任意 | 手動実行 |

---

## 2. ワークフロー定義

### 2.1 ワークフロー構成

`.github/workflows/` ディレクトリに以下のワークフローファイルを配置:

```
.github/workflows/
├── backend-ci.yml       # バックエンドCI
├── frontend-ci.yml      # フロントエンドCI
├── infrastructure.yml   # Terraformインフラ
└── deploy.yml           # デプロイ
```

---

## 3. バックエンド CI タスク

### 3.1 ワークフローファイル: `backend-ci.yml`

**実行条件**:
- `backend/` 配下のファイルが変更された場合
- `main` または `develop` ブランチへの push
- Pull Request の作成・更新

**実行ステップ**:

#### Step 1: リポジトリのチェックアウト
```yaml
- name: Checkout code
  uses: actions/checkout@v4
```

#### Step 2: Java 環境のセットアップ
```yaml
- name: Set up JDK 21
  uses: actions/setup-java@v4
  with:
    distribution: 'temurin'
    java-version: '21'
    cache: 'maven'
```

#### Step 3: Maven 依存関係の解決
```yaml
- name: Resolve dependencies
  working-directory: ./backend
  run: mvn dependency:resolve
```

#### Step 4: コンパイル
```yaml
- name: Build with Maven
  working-directory: ./backend
  run: mvn clean compile
```

#### Step 5: ユニットテストの実行
```yaml
- name: Run unit tests
  working-directory: ./backend
  run: mvn test
```

#### Step 6: テストカバレッジの計算
```yaml
- name: Generate test coverage report
  working-directory: ./backend
  run: mvn jacoco:report
```

#### Step 7: JARファイルのビルド
```yaml
- name: Package application
  working-directory: ./backend
  run: mvn package -DskipTests
```

#### Step 8: アーティファクトのアップロード
```yaml
- name: Upload JAR artifact
  uses: actions/upload-artifact@v4
  with:
    name: ecsite-backend
    path: backend/target/*.jar
```

### 3.2 品質チェック

#### Checkstyle (コーディング規約チェック)
```yaml
- name: Run Checkstyle
  working-directory: ./backend
  run: mvn checkstyle:check
```

#### SpotBugs (バグ検出)
```yaml
- name: Run SpotBugs
  working-directory: ./backend
  run: mvn spotbugs:check
```

#### Dependency Check (脆弱性スキャン)
```yaml
- name: Check for vulnerabilities
  working-directory: ./backend
  run: mvn org.owasp:dependency-check-maven:check
```

---

## 4. フロントエンド CI タスク

### 4.1 ワークフローファイル: `frontend-ci.yml`

**実行条件**:
- `frontend/` 配下のファイルが変更された場合
- `main` または `develop` ブランチへの push
- Pull Request の作成・更新

**実行ステップ**:

#### Step 1: リポジトリのチェックアウト
```yaml
- name: Checkout code
  uses: actions/checkout@v4
```

#### Step 2: Node.js 環境のセットアップ
```yaml
- name: Set up Node.js
  uses: actions/setup-node@v4
  with:
    node-version: '20'
    cache: 'npm'
    cache-dependency-path: frontend/package-lock.json
```

#### Step 3: 依存関係のインストール
```yaml
- name: Install dependencies
  working-directory: ./frontend
  run: npm ci
```

#### Step 4: ESLint (コード品質チェック)
```yaml
- name: Run ESLint
  working-directory: ./frontend
  run: npm run lint
```

#### Step 5: Prettier (コードフォーマットチェック)
```yaml
- name: Check code formatting
  working-directory: ./frontend
  run: npm run format:check
```

#### Step 6: TypeScript 型チェック
```yaml
- name: Type check
  working-directory: ./frontend
  run: npm run type-check
```

#### Step 7: ユニットテストの実行
```yaml
- name: Run unit tests
  working-directory: ./frontend
  run: npm test -- --coverage
```

#### Step 8: ビルド
```yaml
- name: Build application
  working-directory: ./frontend
  run: npm run build
```

#### Step 9: ビルド成果物のアップロード
```yaml
- name: Upload build artifact
  uses: actions/upload-artifact@v4
  with:
    name: ecsite-frontend
    path: frontend/build/
```

---

## 5. インフラストラクチャ CI/CD タスク

### 5.1 ワークフローファイル: `infrastructure.yml`

**実行条件**:
- `terraform/` 配下のファイルが変更された場合
- `main` ブランチへの push
- Pull Request の作成・更新

**実行ステップ**:

#### Step 1: リポジトリのチェックアウト
```yaml
- name: Checkout code
  uses: actions/checkout@v4
```

#### Step 2: Terraform のセットアップ
```yaml
- name: Setup Terraform
  uses: hashicorp/setup-terraform@v3
  with:
    terraform_version: 1.6.0
```

#### Step 3: AWS 認証情報の設定
```yaml
- name: Configure AWS credentials
  uses: aws-actions/configure-aws-credentials@v4
  with:
    aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
    aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
    aws-region: ap-northeast-1
```

#### Step 4: Terraform の初期化
```yaml
- name: Terraform Init
  working-directory: ./terraform
  run: terraform init
```

#### Step 5: Terraform フォーマットチェック
```yaml
- name: Terraform Format
  working-directory: ./terraform
  run: terraform fmt -check
```

#### Step 6: Terraform バリデーション
```yaml
- name: Terraform Validate
  working-directory: ./terraform
  run: terraform validate
```

#### Step 7: Terraform プラン
```yaml
- name: Terraform Plan
  working-directory: ./terraform
  run: terraform plan -out=tfplan
```

#### Step 8: Terraform Apply (main ブランチのみ)
```yaml
- name: Terraform Apply
  if: github.ref == 'refs/heads/main' && github.event_name == 'push'
  working-directory: ./terraform
  run: terraform apply -auto-approve tfplan
```

---

## 6. デプロイタスク

### 6.1 ワークフローファイル: `deploy.yml`

**実行条件**:
- `main` ブランチへの push
- 手動トリガー (`workflow_dispatch`)

**実行ステップ**:

#### Step 1: アーティファクトのダウンロード
```yaml
- name: Download backend artifact
  uses: actions/download-artifact@v4
  with:
    name: ecsite-backend

- name: Download frontend artifact
  uses: actions/download-artifact@v4
  with:
    name: ecsite-frontend
```

#### Step 2: バックエンドのデプロイ (EC2 へ)
```yaml
- name: Deploy backend to EC2
  uses: appleboy/scp-action@master
  with:
    host: ${{ secrets.EC2_HOST }}
    username: ec2-user
    key: ${{ secrets.EC2_SSH_KEY }}
    source: "*.jar"
    target: "/home/ec2-user/app/"
```

#### Step 3: アプリケーションの再起動
```yaml
- name: Restart Spring Boot application
  uses: appleboy/ssh-action@master
  with:
    host: ${{ secrets.EC2_HOST }}
    username: ec2-user
    key: ${{ secrets.EC2_SSH_KEY }}
    script: |
      sudo systemctl restart ecsite-backend
      sudo systemctl status ecsite-backend
```

#### Step 4: フロントエンドのデプロイ (S3 + CloudFront)
```yaml
- name: Deploy frontend to S3
  run: |
    aws s3 sync ./frontend/build/ s3://${{ secrets.S3_BUCKET }}/ --delete
    aws cloudfront create-invalidation --distribution-id ${{ secrets.CLOUDFRONT_DIST_ID }} --paths "/*"
```

---

## 7. 通知タスク

### 7.1 Slack 通知

**デプロイ成功時の通知**:
```yaml
- name: Notify Slack on success
  if: success()
  uses: slackapi/slack-github-action@v1
  with:
    webhook-url: ${{ secrets.SLACK_WEBHOOK_URL }}
    payload: |
      {
        "text": "✅ Deployment successful!",
        "blocks": [
          {
            "type": "section",
            "text": {
              "type": "mrkdwn",
              "text": "*Deployment Status:* Success\n*Branch:* ${{ github.ref }}\n*Commit:* ${{ github.sha }}"
            }
          }
        ]
      }
```

**デプロイ失敗時の通知**:
```yaml
- name: Notify Slack on failure
  if: failure()
  uses: slackapi/slack-github-action@v1
  with:
    webhook-url: ${{ secrets.SLACK_WEBHOOK_URL }}
    payload: |
      {
        "text": "❌ Deployment failed!",
        "blocks": [
          {
            "type": "section",
            "text": {
              "type": "mrkdwn",
              "text": "*Deployment Status:* Failed\n*Branch:* ${{ github.ref }}\n*Commit:* ${{ github.sha }}\n*View logs:* ${{ github.server_url }}/${{ github.repository }}/actions/runs/${{ github.run_id }}"
            }
          }
        ]
      }
```

---

## 8. GitHub Secrets 設定

### 8.1 必要な Secrets

| Secret 名 | 説明 | 例 |
|-----------|------|-----|
| `AWS_ACCESS_KEY_ID` | AWS アクセスキーID | `AKIAIOSFODNN7EXAMPLE` |
| `AWS_SECRET_ACCESS_KEY` | AWS シークレットアクセスキー | `wJalrXUtnFEMI/K7MDENG/...` |
| `EC2_HOST` | EC2 インスタンスのパブリックIP | `54.249.xxx.xxx` |
| `EC2_SSH_KEY` | EC2 SSH 秘密鍵 | `-----BEGIN RSA PRIVATE KEY-----...` |
| `S3_BUCKET` | S3 バケット名 | `ecsite-frontend-prod` |
| `CLOUDFRONT_DIST_ID` | CloudFront ディストリビューションID | `E1A2B3C4D5E6F7` |
| `SLACK_WEBHOOK_URL` | Slack Webhook URL | `https://hooks.slack.com/services/...` |

### 8.2 Secrets の設定方法

1. GitHub リポジトリの **Settings** → **Secrets and variables** → **Actions**
2. **New repository secret** をクリック
3. Name と Secret を入力して保存

---

## 9. ブランチ保護ルール

### 9.1 `main` ブランチの保護設定

**Settings** → **Branches** → **Branch protection rules**:

- [ ] **Require a pull request before merging**
  - [ ] Require approvals (最低1人)
- [ ] **Require status checks to pass before merging**
  - [ ] `backend-ci`
  - [ ] `frontend-ci`
  - [ ] `infrastructure`
- [ ] **Require conversation resolution before merging**
- [ ] **Do not allow bypassing the above settings**

---

## 10. 実装チェックリスト

### Phase 6 実装時

- [ ] `.github/workflows/backend-ci.yml` の作成
- [ ] `.github/workflows/frontend-ci.yml` の作成
- [ ] `.github/workflows/infrastructure.yml` の作成
- [ ] `.github/workflows/deploy.yml` の作成
- [ ] GitHub Secrets の設定
- [ ] ブランチ保護ルールの設定
- [ ] Slack Webhook の設定
- [ ] ワークフローの動作確認

### テスト項目

- [ ] `push` 時に CI が自動実行されること
- [ ] Pull Request 作成時に CI が自動実行されること
- [ ] テスト失敗時に CI が失敗すること
- [ ] `main` ブランチへの push でデプロイが実行されること
- [ ] Slack 通知が正しく送信されること

---

## 11. トラブルシューティング

### CI が失敗する場合

1. **ログの確認**: Actions タブでワークフロー実行ログを確認
2. **依存関係エラー**: `mvn clean install` や `npm ci` を再実行
3. **テストエラー**: ローカルで `mvn test` や `npm test` を実行して確認
4. **権限エラー**: GitHub Secrets の設定を確認

### デプロイが失敗する場合

1. **AWS 認証情報**: Secrets の `AWS_ACCESS_KEY_ID` と `AWS_SECRET_ACCESS_KEY` を確認
2. **EC2 接続エラー**: SSH 秘密鍵とホスト名を確認
3. **S3 アップロードエラー**: S3 バケットのポリシーを確認

---

## 参考資料

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Terraform GitHub Actions](https://github.com/hashicorp/setup-terraform)
- [AWS Actions](https://github.com/aws-actions)

---

## 関連ドキュメント

- [インフラ設計書](./infrastructure-design.md)
- [セキュリティ要件書](./security-requirements.md)
- [監視要件書](./monitoring-requirements.md)
