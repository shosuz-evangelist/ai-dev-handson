# Constitution.md（プロジェクト憲章）— AI駆動開発トレーニング / ECサイト

> 目的：受講者が **Spec-driven（仕様書起点）→ Spec Kit で spec 生成 → GitHub Copilot で実装** を体験し、
> 「仕様→コード」の往復を自走できる状態になること。

---

## 1. プロジェクトの目的とスコープ

### 1.1 ゴール（研修のゴール）

- 仕様書（spec）を唯一の根拠として実装を進める（Spec-driven Development）
- 受講者が Spec Kit を使って spec ファイルを生成し、その spec を Copilot に渡して実装を進める

### 1.2 Day1 / Day2 の扱い

- **Day1**：Phase 0〜7（環境確認〜Product/Cart まで）
- **Day2**：応用（インフラ設計/セキュリティ/監視/タスク）＋ **Customer / Sales 追加**

---

## 2. 仕様書駆動開発（最重要ルール）

- **仕様書が唯一の信頼情報源**：実装は spec の記述に従う
- Copilot Chat には、必ず spec を前提として渡す（例：`@workspace` で spec を読ませる）
- 仕様が曖昧な場合は、勝手に実装を拡張せず、仕様側を明確化する

---

## 3. セキュリティ / 品質（止まらない最小）

- 入力は必ずバリデーション（Bean Validation）
- 機密情報をログ・レスポンスに含めない
- 最低限のテスト（Controller/Serviceの基本パス）

---

## 4. リポジトリ構成（Source of Truth）

このプロジェクトで、仕様書の Source of Truth は **`.github/spec/`**。
ただし、配布物は解凍時に見失わないよう **`spec/` で配布し、配置時に `.github/spec/` へコピー**する。

```text
ai-driven-dev-day2/
├── .github/
│   └── spec/                    # 仕様書 (最重要)
│       ├── Constitution.md      # このファイル (プロジェクト憲章)
│       ├── reference/           # Day2 模範解答（完成版 spec）
│       │   ├── phase-0-environment-check.md
│       │   ├── phase-1-hello-api.md
│       │   ├── phase-2-database-connection.md
│       │   ├── phase-3-product-list-api.md
│       │   ├── phase-4-product-detail-api.md
│       │   ├── phase-5-product-create-api.md
│       │   ├── phase-6-cart-design.md
│       │   ├── phase-7-cart-api.md
│       │   ├── product-api-spec.md
│       │   ├── cart-api-spec.md
│       │   ├── infrastructure-design.md
│       │   ├── security-requirements.md
│       │   ├── tasks.md
│       │   ├── monitoring-requirements.md
│       │   ├── backend-requirements-customer-sales.txt
│       │   ├── frontend-requirements.txt
│       │   ├── day2-ec-customers-table.md
│       │   ├── day2-ec-sales-table.md
│       │   ├── day2-ec-customer-api-spec.md
│       │   ├── day2-ec-sales-api-spec.md
│       │   ├── day2-ec-auth-api-spec.md
│       │   └── day2-ec-cart-api-spec.md
│       └── generated/            # 受講者が Spec Kit で生成（空ディレクトリ）
│
├── backend/                      # Day1 完成版コード
│   ├── src/main/java/com/example/ecsite/
│   │   ├── controller/
│   │   │   ├── HelloController.java      ✅ Day1実装済み
│   │   │   ├── ProductController.java    ✅ Day1実装済み
│   │   │   └── CartController.java       ✅ Day1実装済み
│   │   ├── entity/
│   │   │   ├── Product.java              ✅ Day1実装済み
│   │   │   ├── Cart.java                 ✅ Day1実装済み
│   │   │   └── CartItem.java             ✅ Day1実装済み
│   │   ├── repository/
│   │   │   ├── ProductRepository.java    ✅ Day1実装済み
│   │   │   ├── CartRepository.java       ✅ Day1実装済み
│   │   │   └── CartItemRepository.java   ✅ Day1実装済み
│   │   └── service/
│   │       ├── ProductService.java       ✅ Day1実装済み
│   │       └── CartService.java          ✅ Day1実装済み
│   └── src/main/resources/
│       ├── application.properties        ✅ Day1実装済み
│       └── schema.sql                    ✅ Day1実装済み (products, carts, cart_items)
│
├── docs/
│   └── prompts/                          # Day2 実装手順
│       ├── day2-backend.md               ✅ Backend 実装プロンプト
│       ├── day2-frontend.md              ✅ Frontend 実装プロンプト
│       └── day2-infra.md                 ✅ インフラ設計プロンプト
│
└── README.md                             ✅ Day1/Day2 説明
```

---

## 5. Day1 完成状態（このリポジトリの初期状態）

このリポジトリは **Day1完成状態** を提供します。受講者全員がこの状態から Day2 の実装を開始します。

### 5.1 Day1 実装済み機能

#### バックエンド

- **Product API（CRUD）**
  - `GET /api/products` - 商品一覧取得
  - `GET /api/products/{id}` - 商品詳細取得
  - `POST /api/products` - 商品登録
- **Cart API（匿名カート）**
  - `POST /api/cart` - カート作成
  - `GET /api/cart/items?cartId={cartId}` - カート内商品一覧取得
  - `POST /api/cart/checkout` - チェックアウト（カート削除）

#### データベース

- `products` テーブル（20件のサンプルデータ投入済み）
- `carts` テーブル
- `cart_items` テーブル

---

## 6. Day2 実装予定機能（受講生が Spec Kit + Copilot で実装）

受講生は Day1完成状態から続けて、以下の機能を **Spec Kit で .md 生成 → @workspace で Copilot に実装させる** フローで実装します。

### 6.1 Day2 Backend 実装フロー

```
Step 1: docs/prompts/day2-backend.md を開く
Step 2: backend-requirements-customer-sales.txt の内容を Copilot Chat に貼り付け
Step 3: /speckit.specify を実行
Step 4: .github/spec/generated/ に以下が生成される
  - day2-ec-customers-table.md
  - day2-ec-sales-table.md
  - day2-ec-customer-api-spec.md
  - day2-ec-sales-api-spec.md
Step 5: @workspace で生成された .md を参照し、Copilot が以下を実装
  - Customer Entity, Sale Entity
  - CustomerRepository, SaleRepository
  - CustomerService, SaleService
  - CustomerController, SaleController
  - AuthController
Step 6: schema.sql に customers, sales テーブルを追加
```

### 6.2 Day2 Frontend 実装フロー

```
Step 1: docs/prompts/day2-frontend.md を開く
Step 2: frontend-requirements.txt の内容を Copilot Chat に貼り付け
Step 3: /speckit.specify を実行
Step 4: .github/spec/generated/ に仕様書が生成される
Step 5: @workspace で生成された .md を参照し、Copilot が以下を実装
  - frontend/ ディレクトリを作成
  - React 18 + TypeScript + Vite プロジェクト構築
  - AuthContext.tsx（認証状態管理）
  - LoginPage.tsx（ログイン画面）
  - ProductListPage.tsx, ProductDetailPage.tsx（商品画面）
  - CartPage.tsx（カート画面）
  - CustomersPage.tsx（顧客管理画面）
  - SalesPage.tsx（売上管理画面）
  - React Router v6 によるルーティング
```

### 6.3 Day2 Infrastructure 設計フロー

```
Step 1: docs/prompts/day2-infra.md を開く
Step 2: インフラ要件を Copilot Chat に貼り付け
Step 3: /speckit.specify を実行
Step 4: .github/spec/generated/infrastructure-design.md が生成される
Step 5: 生成された .md と .github/spec/reference/infrastructure-design.md を比較
Step 6: 設計レビュー（Terraform 実行なし）
  - 推奨構成: EC2 + nginx + RDS PostgreSQL 14.20
  - コスト見積もり: 約 $50/月
```

### 6.4 Day2 実装対象機能一覧

#### バックエンド（Day2実装対象）

- **認証 API**
  - `POST /api/auth/login` - ログイン（email + password）
- **Customer API（CRUD）**
  - `GET /api/customers` - 顧客一覧取得
  - `GET /api/customers/{id}` - 顧客詳細取得
  - `POST /api/customers` - 顧客新規登録
  - `PUT /api/customers/{id}` - 顧客情報更新
- **Sales API（CRUD/集計）**
  - `GET /api/sales` - 売上一覧取得（customerName, productName を含む）
  - `GET /api/sales/{id}` - 売上詳細取得
  - `POST /api/sales` - 売上新規登録
- **Cart API の改修（userId 紐づけ）**
  - `GET /api/carts/{userId}` - ユーザーのカート取得
  - `POST /api/carts/{userId}/items` - カートに商品追加
  - `PUT /api/carts/{userId}/items/{itemId}` - カート内商品の数量更新
  - `DELETE /api/carts/{userId}/items/{itemId}` - カート内商品削除
  - `POST /api/carts/{userId}/checkout` - 購入確定（Sales自動登録、カートクリア）

#### データベース拡張（Day2実装対象）

- `customers` テーブル追加
  - id, name, email, created_at, updated_at
- `sales` テーブル追加
  - id, customer_id, product_id, quantity, price, total, created_at, updated_at
- `carts` テーブルに `customer_id` カラム追加

#### フロントエンド（Day2実装対象 - ゼロから実装）

- **認証機能**
  - ログイン画面（LoginPage.tsx）
  - 認証状態管理（AuthContext.tsx - Context API）
- **商品機能**
  - 商品一覧画面（ProductListPage.tsx）
  - 商品詳細画面（ProductDetailPage.tsx）
- **カート機能**
  - カート管理画面（CartPage.tsx）
  - 数量変更、削除、購入確定
- **顧客管理機能**
  - 顧客一覧画面（CustomersPage.tsx）
  - 顧客詳細・新規作成・編集
- **売上管理機能**
  - 売上一覧画面（SalesPage.tsx）
  - 売上詳細・新規作成
- **共通機能**
  - Header コンポーネント（ナビゲーション、ログイン状態表示）
  - React Router v6 による画面遷移
  - Vite プロキシ設定（/api → http://localhost:8080）

#### インフラ設計（Day2実装対象）

- Spec Kit で infrastructure-design.md を生成
- 設計レビュー（Terraform 実行なし）
- 推奨構成: **EC2 + nginx + RDS PostgreSQL 14.20**
- コスト見積もり: 約 $50/月
- VPC, ALB, Security Groups, CloudWatch の設計

---

## 7. Day2 完成版 spec（模範解答）

`.github/spec/reference/` に講師が用意した **完成版 spec（模範解答）** が配置されています。

### DB（テーブル定義）

- `day2-ec-customers-table.md`（customers テーブル定義）
- `day2-ec-sales-table.md`（sales テーブル定義：price/total を含む）

### API

- `day2-ec-customer-api-spec.md`（Customer API 仕様）
- `day2-ec-sales-api-spec.md`（Sales API 仕様）
- `day2-ec-auth-api-spec.md`（Auth API 仕様）
- `day2-ec-cart-api-spec.md`（Cart API 仕様）

### 要件定義

- `backend-requirements-customer-sales.txt`（Backend 要件定義 - TOML 形式）
- `frontend-requirements.txt`（Frontend 要件定義）

### インフラ

- `infrastructure-design.md`（完成版インフラ設計書）

> 受講者は Spec Kit で生成した `.github/spec/generated/` と `.github/spec/reference/` を比較し、差分を確認します。

---

## 8. 参照（Day2の既存仕様）

Day2 の既存仕様（Product/Cart/Infra/Security/Monitoring/Tasks）は `.github/spec/reference/` 配下に存在します。
Customer/Sales 追加は day2-ec-\*.md を"追加 spec"として扱い、Copilot の入力に含めます。

---

## 9. 重要な設計判断

### 9.1 sales テーブルの price/total 保存方針

- **price（単価）**: 購入時点の products.price をコピーして保存（後日の価格変更の影響を受けない）
- **total（合計金額）**: quantity \* price を計算して保存（監査・価格変動対策・再計算コスト削減）

### 9.2 インフラ推奨構成

- **EC2 + nginx** を推奨（ECS Fargate より確実に動作）
- **RDS PostgreSQL 14.20**（14.11 は古くエラーになるため）
- **Public Subnet 配置**（学習環境のため、管理用アクセスを容易にする）
- **Multi-AZ 無効**（コスト削減のため Single-AZ）
- **Terraform 実行なし**（設計レビューのみ）

### 9.3 認証方式

- **平文パスワード**（学習環境のため簡易実装）
- **本番環境では暗号化必須**

---

## 10. トラブルシューティング

### 10.1 Spec Kit が生成しない

- プロンプトを **そのままコピー** して Copilot Chat に貼り付ける
- `/speckit.specify` を実行
- 詰まった場合は `.github/spec/reference/` の完成版を使用

### 10.2 @workspace が効かない

- 生成された .md ファイルをエディタで開く
- Copilot Chat を再度開く

### 10.3 Day2 テーブルが作成されない

```bash
psql -U postgres -d ecsite -f backend/src/main/resources/schema.sql
```

---

🎉 **Day1完成状態 → Spec Kit で Day2実装 → Day2完成！**
