# Day2 Copilot プロンプト実行ガイド

このファイルは、Day2の各演習で **GitHub Copilot Chat に何を指示すればいいか** をまとめたガイドです。

---

## 📋 基本的な使い方

### Copilot Chat の起動
1. VS Code のサイドバーから **Copilot Chat** アイコンをクリック
2. または `Ctrl+Shift+I` (Windows/Linux) / `Cmd+Shift+I` (Mac)

### @workspace の使い方
- `@workspace` をつけると、プロジェクト全体のコードを参照できます
- `/spec` コマンドで仕様書ファイルを明示的に参照できます

### 例
```
@workspace /spec .github/spec/reference/day2-ec-customer-api-spec.md を参照して、CustomerControllerを実装してください。
```

---

## 演習1: DB拡張 + Backend API実装

### ステップ1: データベーステーブル作成

#### 1-1. customers テーブル作成

**プロンプト:**
```
@workspace /spec .github/spec/reference/day2-ec-customers-table.md を参照して、customersテーブルのマイグレーションファイルを src/main/resources/db/migration/ に作成してください。ファイル名は V2__create_customers_table.sql としてください。
```

**期待される出力:**
- `V2__create_customers_table.sql` が作成される
- `id`, `name`, `email`, `created_at`, `updated_at` カラムが含まれる
- `email` に UNIQUE 制約がある

---

#### 1-2. sales テーブル作成

**プロンプト:**
```
@workspace /spec .github/spec/reference/day2-ec-sales-table.md を参照して、salesテーブルのマイグレーションファイルを src/main/resources/db/migration/ に作成してください。ファイル名は V3__create_sales_table.sql としてください。
```

**期待される出力:**
- `V3__create_sales_table.sql` が作成される
- `customer_id`, `product_id` の外部キー制約がある
- `quantity`, `price`, `total` の CHECK 制約がある
- インデックスが追加される

---

### ステップ2: Customer API 実装

#### 2-1. Entity / Repository 作成

**プロンプト:**
```
@workspace /spec .github/spec/reference/day2-ec-customer-api-spec.md を参照して、Customer エンティティと CustomerRepository を作成してください。エンティティは com.example.demo.entity パッケージ、リポジトリは com.example.demo.repository パッケージに配置してください。
```

**期待される出力:**
- `Customer.java` (Entity)
- `CustomerRepository.java` (JPA Repository)
- `email` の UNIQUE 制約が `@Column(unique = true)` で定義される

---

#### 2-2. Service 作成

**プロンプト:**
```
@workspace /spec .github/spec/reference/day2-ec-customer-api-spec.md を参照して、CustomerService を作成してください。以下の機能を実装してください：
1. 顧客一覧取得
2. 顧客詳細取得（存在しない場合は例外をスロー）
3. 顧客登録（emailの重複チェック）
4. 顧客更新（存在チェック + emailの重複チェック）

パッケージは com.example.demo.service としてください。
```

**期待される出力:**
- `CustomerService.java`
- メールアドレス重複時に `IllegalArgumentException` をスロー
- 顧客が存在しない場合に `EntityNotFoundException` をスロー

---

#### 2-3. Controller 作成

**プロンプト:**
```
@workspace /spec .github/spec/reference/day2-ec-customer-api-spec.md を参照して、CustomerController を作成してください。
- Base URL: /api/customers
- GET /api/customers → 200
- GET /api/customers/{id} → 200 / 404
- POST /api/customers → 201 / 400 / 409
- PUT /api/customers/{id} → 200 / 400 / 404 / 409

エラーハンドリングは GlobalExceptionHandler で統一してください。
```

**期待される出力:**
- `CustomerController.java`
- Request/Response DTO
- Bean Validation (`@NotBlank`, `@Email`)

---

#### 2-4. テスト作成

**プロンプト:**
```
@workspace CustomerService のユニットテストを作成してください。以下のケースをカバーしてください：
1. 顧客登録成功
2. メールアドレス重複エラー (409)
3. 顧客詳細取得成功
4. 顧客が存在しない (404)
5. バリデーションエラー (400)

MockitoでCustomerRepositoryをモック化してください。
```

---

### ステップ3: Sales API 実装

#### 3-1. Entity / Repository 作成

**プロンプト:**
```
@workspace /spec .github/spec/reference/day2-ec-sales-table.md を参照して、Sale エンティティと SaleRepository を作成してください。
- customerId, productId は @ManyToOne で Customer/Product と関連付け
- total は保存時に quantity * price で計算して格納

パッケージは com.example.demo.entity と com.example.demo.repository としてください。
```

---

#### 3-2. Service 作成

**プロンプト:**
```
@workspace /spec .github/spec/reference/day2-ec-sales-api-spec.md を参照して、SaleService を作成してください。
- POST /api/sales: customerId/productId の存在チェック、total = quantity * price の計算
- GET /api/sales: 売上一覧取得（customerName と productName を JOIN で取得）

パッケージは com.example.demo.service としてください。
```

---

#### 3-3. Controller 作成

**プロンプト:**
```
@workspace /spec .github/spec/reference/day2-ec-sales-api-spec.md を参照して、SaleController を作成してください。
- Base URL: /api/sales
- POST /api/sales → 201 / 400 / 404
- GET /api/sales → 200

エラーハンドリングは GlobalExceptionHandler で統一してください。
```

---

#### 3-4. 動作確認

**プロンプト:**
```
@workspace 以下のcurlコマンドでAPIをテストしたいです。期待されるレスポンスを教えてください。

# 顧客作成
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{"name":"Taro","email":"taro@example.com"}'

# 売上登録
curl -X POST http://localhost:8080/api/sales \
  -H "Content-Type: application/json" \
  -d '{"customerId":1,"productId":1,"quantity":2}'

# 売上一覧
curl http://localhost:8080/api/sales
```

---

## 演習2: React Frontend 実装

### 事前準備: プロジェクト構成確認

**プロンプト:**
```
@workspace Reactプロジェクトの構成を教えてください。src/components/ と src/pages/ はありますか？なければ作成してください。
```

---

### ステップ1: 商品一覧画面

#### 1-1. ProductList コンポーネント作成

**プロンプト:**
```
@workspace /spec .github/spec/reference/product-list-spec.md を参照して、ProductList コンポーネントを src/pages/ProductList.tsx に作成してください。
- axios で GET /api/products を呼び出し
- 商品一覧をカード形式で表示
- エラー時はエラーメッセージを表示

TypeScript + React Hooks (useState, useEffect) で実装してください。
```

---

#### 1-2. API クライアント作成

**プロンプト:**
```
@workspace axios の設定ファイル src/api/client.ts を作成してください。
- baseURL: http://localhost:8080
- withCredentials: true (Cookie認証のため)
- タイムアウト: 10秒
```

---

### ステップ2: ログイン画面

#### 2-1. LoginForm コンポーネント作成

**プロンプト:**
```
@workspace /spec .github/spec/reference/customer-login-spec.md を参照して、LoginForm コンポーネントを src/pages/LoginForm.tsx に作成してください。
- email と password の入力フォーム
- POST /api/auth/login でログイン
- 成功時は / にリダイレクト
- エラー時はエラーメッセージを表示

React Hook Form でバリデーションを実装してください。
```

---

### ステップ3: カート画面

#### 3-1. Cart コンポーネント作成

**プロンプト:**
```
@workspace /spec .github/spec/reference/cart-spec.md を参照して、Cart コンポーネントを src/pages/Cart.tsx に作成してください。
- GET /api/cart でカート内容を取得
- 数量変更: PUT /api/cart/items/{itemId}
- 削除: DELETE /api/cart/items/{itemId}
- チェックアウト: POST /api/cart/checkout

TypeScript + React Hooks で実装してください。
```

---

### ステップ4: 顧客登録画面

**プロンプト:**
```
@workspace /spec .github/spec/reference/customer-register-spec.md を参照して、CustomerRegister コンポーネントを src/pages/CustomerRegister.tsx に作成してください。
- name と email の入力フォーム
- POST /api/customers で登録
- 成功時はログイン画面にリダイレクト

React Hook Form + Yup でバリデーションを実装してください。
```

---

### ステップ5: 注文履歴画面

**プロンプト:**
```
@workspace /spec .github/spec/reference/order-history-spec.md を参照して、OrderHistory コンポーネントを src/pages/OrderHistory.tsx に作成してください。
- GET /api/sales?customerId={id} で売上一覧を取得
- テーブル形式で表示（日付、商品名、数量、単価、合計）

TypeScript + React Hooks で実装してください。
```

---

### ステップ6: ルーティング設定

**プロンプト:**
```
@workspace React Router v6 で以下のルーティングを設定してください。
- / → ProductList
- /login → LoginForm
- /register → CustomerRegister
- /cart → Cart
- /orders → OrderHistory

src/App.tsx を更新してください。
```

---

### デバッグ時のプロンプト

#### CORS エラーが出た場合

**プロンプト:**
```
@workspace Spring Boot の CORS 設定を確認してください。WebMvcConfigurer で http://localhost:3000 からのリクエストを許可するように設定してください。
```

---

#### Cookie が送信されない場合

**プロンプト:**
```
@workspace axios のリクエストに withCredentials: true が設定されているか確認してください。また、Spring Boot 側で SameSite=None; Secure の設定がされているか確認してください。
```

---

#### 型エラーが出た場合

**プロンプト:**
```
@workspace 以下のTypeScriptエラーを修正してください。

[エラーメッセージをコピペ]
```

---

## 演習3: Spec Kit でインフラ仕様作成

### ステップ1: requirements.txt 作成

**プロンプト:**
```
@workspace /spec Terraform を使って以下のAWSリソースのインフラ仕様書を生成したいです。requirements.txt の内容を提案してください。

必要なリソース:
- VPC (10.0.0.0/16)
- Public Subnet x2 (AZ分散)
- Private Subnet x2 (AZ分散)
- Internet Gateway
- NAT Gateway
- RDS (PostgreSQL 14)
- ECS Fargate (Spring Boot アプリ)
- Application Load Balancer
- Security Group (ALB, ECS, RDS)
- CloudWatch Logs
```

---

### ステップ2: Spec Kit で仕様書生成

**プロンプト:**
```
@workspace /speckit.specify

[requirements.txt の中身を貼り付け]
```

**期待される出力:**
- `.github/spec/infrastructure-design.md`

---

### ステップ3: 仕様書の確認

**プロンプト:**
```
@workspace .github/spec/infrastructure-design.md を確認してください。以下の項目が含まれているか教えてください：
1. VPC の CIDR ブロック
2. Subnet の構成（Public/Private、AZ分散）
3. RDS のインスタンスタイプとバックアップ設定
4. ECS のタスク定義（CPU/メモリ）
5. Security Group のルール（ALB, ECS, RDS）
```

---

## 演習4: CI/CD (GitHub Actions)

### ステップ1: requirements.txt 作成

**プロンプト:**
```
@workspace GitHub Actions で以下のCI/CDパイプラインを構築したいです。requirements.txt の内容を提案してください。

要件:
- プルリクエスト時にテスト実行 (mvn test)
- main ブランチへのマージ時にビルド + デプロイ
- テスト失敗時は Slack 通知
- 成果物は GitHub Artifacts に保存
```

---

### ステップ2: Spec Kit で仕様書生成

**プロンプト:**
```
@workspace /speckit.specify

[requirements.txt の中身を貼り付け]
```

**期待される出力:**
- `.github/spec/cicd-tasks.md`

---

### ステップ3: GitHub Actions ワークフロー作成

**プロンプト:**
```
@workspace /spec .github/spec/cicd-tasks.md を参照して、GitHub Actions のワークフローファイル .github/workflows/build.yml を作成してください。

要件:
- Java 17 のセットアップ
- Maven キャッシュの利用
- テスト実行 (mvn test)
- カバレッジレポート生成 (JaCoCo)
- 成果物のアップロード
```

---

### デバッグ時のプロンプト

#### ワークフローが失敗した場合

**プロンプト:**
```
@workspace GitHub Actions のワークフローが以下のエラーで失敗しました。修正方法を教えてください。

[エラーログをコピペ]
```

---

## 演習5: 運用・監視 (CloudWatch)

### ステップ1: requirements.txt 作成

**プロンプト:**
```
@workspace CloudWatch で以下の監視を設定したいです。requirements.txt の内容を提案してください。

要件:
- ECS タスクのCPU/メモリ使用率
- RDS の接続数/レイテンシ
- ALB のリクエスト数/エラー率
- アプリケーションログの収集
- エラーログのアラート (SNS 通知)
```

---

### ステップ2: Spec Kit で仕様書生成

**プロンプト:**
```
@workspace /speckit.specify

[requirements.txt の中身を貼り付け]
```

**期待される出力:**
- `.github/spec/monitoring-plan.md`

---

### ステップ3: CloudWatch 設定確認

**プロンプト:**
```
@workspace .github/spec/monitoring-plan.md を確認してください。以下の項目が含まれているか教えてください：
1. メトリクスの種類と閾値
2. アラームの設定（SNS通知先）
3. ログ保持期間
4. ダッシュボードの構成
```

---

## 🛠️ トラブルシューティング

### Copilot が反応しない場合

1. **リロード**: VS Code をリロード (`Ctrl+Shift+P` → "Reload Window")
2. **認証確認**: GitHub Copilot の認証が有効か確認
3. **@workspace を外す**: `@workspace` を外してシンプルなプロンプトで試す

---

### 期待した出力が得られない場合

**プロンプトを具体化:**
```
@workspace 以下の仕様で CustomerController を実装してください。

【必須要件】
- Base URL: /api/customers
- POST /api/customers → 201 / 400 / 409
- GET /api/customers → 200
- Bean Validation を使用
- GlobalExceptionHandler でエラーハンドリング

【サンプルコード】
[他のControllerのコードを貼り付け]
```

---

### コードが長すぎてエラーになる場合

**段階的に生成:**
```
@workspace まず CustomerController の POST エンドポイントだけを実装してください。
```

→ 確認後

```
@workspace 次に GET /api/customers エンドポイントを追加してください。
```

---

## 📚 参考: 効果的なプロンプトの書き方

1. **@workspace を活用**: プロジェクト全体を参照させる
2. **仕様書を明示**: `/spec [ファイルパス]` で仕様書を参照
3. **具体的に指示**: 「〇〇を作成してください」ではなく「〇〇を△△パッケージに、□□の機能を含めて作成してください」
4. **サンプルを提示**: 既存のコードを参考にさせる
5. **段階的に生成**: 一度に全部作ろうとせず、機能ごとに分割

---

## ✅ チェックリスト

演習開始前に確認:

- [ ] Copilot Chat が起動できる
- [ ] @workspace コマンドが使える
- [ ] .github/spec/reference/ に仕様書がある
- [ ] プロジェクトがビルドできる

---

**困ったときは、このガイドに戻ってプロンプトをコピペしてください！** 🚀
