# Day2: Backend実装（DB拡張 + Customer/Sales API）

## 🎯 演習の目標
Day1で完成したProduct/Cart APIに、Customer管理とSales管理を追加し、Spec Kitで生成した仕様書を「唯一の真実源（Source of Truth）」として実装する。

---

## 📋 前提条件
- Day1完成版が動作している（Product API + Cart API）
- PostgreSQL 15+ が起動している
- OpenJDK 21+ がインストール済み
- Maven 3.9+ がインストール済み
- GitHub Copilot Chat が有効
- Spec Kit がインストール済み

---

## 🚀 Phase 0: Spec Kit で仕様書生成

### ステップ1: Copilot Chat を開く
VS Code で **Ctrl+Shift+I**（または Cmd+Shift+I）を押して、GitHub Copilot Chat を開く。

---

### ステップ2: backend-requirements.txt の中身を貼り付け

**重要**: ファイルパスではなく、**テキストの中身**をコピーして貼る。

`.github/spec/reference/backend-requirements-customer-sales.txt` を開き、以下の内容をコピーして Copilot Chat に貼り付けてください。

```toml
# Day2 EC Backend Requirements: Customer/Sales API

[day2-ec-backend]
title = "Day2 EC: Customer/Sales 追加 (DB + API 仕様書生成)"
generate = [
  { path = ".github/spec/generated/day2-ec-customers-table.md", kind = "database-schema" },
  { path = ".github/spec/generated/day2-ec-sales-table.md", kind = "database-schema" },
  { path = ".github/spec/generated/day2-ec-customer-api-spec.md", kind = "api-spec" },
  { path = ".github/spec/generated/day2-ec-sales-api-spec.md", kind = "api-spec" }
]

constraints = [
  "Source of truth is .github/spec/*.md",
  "Security-first: validate all inputs; do not log secrets",
  "TDD: Controller/Service tests target 80% coverage",
  "REST: Use standard HTTP status (200, 400, 404, 500)"
]

## Customer API
- GET /api/customers
- GET /api/customers/{id}
- POST /api/customers
- PUT /api/customers/{id}

## Sales API
- GET /api/sales (顧客名・商品名を含む)
- GET /api/sales/{id}
- POST /api/sales

## データモデル
### Customer
- id: BIGSERIAL PRIMARY KEY
- name: VARCHAR(100) NOT NULL
- email: VARCHAR(255) UNIQUE NOT NULL
- password: VARCHAR(255) NOT NULL
- phone: VARCHAR(20)
- created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP

### Sale
- id: BIGSERIAL PRIMARY KEY
- customer_id: BIGINT NOT NULL REFERENCES customer(id)
- product_id: BIGINT NOT NULL REFERENCES products(id)
- quantity: INT NOT NULL
- sale_date: TIMESTAMP DEFAULT CURRENT_TIMESTAMP
- total_amount: DECIMAL(10,2) NOT NULL

## テーブル作成順序（重要）
1. products（既存）
2. customer
3. sale（customer_id と product_id の外部キーを参照）
```

---

### ステップ3: /speckit.specify を実行

Copilot Chat に以下を入力：

```
/speckit.specify
```

**期待される出力**: `.github/spec/generated/` 配下に以下の4ファイルが生成される

1. `day2-ec-customers-table.md`
2. `day2-ec-sales-table.md`
3. `day2-ec-customer-api-spec.md`
4. `day2-ec-sales-api-spec.md`

---

### ステップ4: 生成されたファイルを確認

```bash
ls -la .github/spec/generated/
```

✅ 4つのファイルが存在することを確認してください。

---

### ステップ5: reference/ と generated/ を比較（重要）

講師が事前に用意した **完成版** `.github/spec/reference/day2-ec-*.md` と、受講生が生成した `.github/spec/generated/day2-ec-*.md` を比較します。

#### 比較方法
```bash
# customers-table.md を比較
diff .github/spec/reference/day2-ec-customers-table.md .github/spec/generated/day2-ec-customers-table.md

# customer-api-spec.md を比較
diff .github/spec/reference/day2-ec-customer-api-spec.md .github/spec/generated/day2-ec-customer-api-spec.md
```

#### 確認ポイント
- [ ] テーブル定義が一致しているか？
- [ ] API仕様（エンドポイント、リクエスト/レスポンス）が一致しているか？
- [ ] エラーハンドリングが含まれているか？
- [ ] テストケースが含まれているか？

#### ⚠️ もし Spec Kit が詰まったら
`.github/spec/reference/day2-ec-*.md` を「正」として、実装を進めてください。

---

## 🚀 Phase 1: Backend 実装

### ステップ1: schema.sql にテーブル追加

#### GitHub Copilot プロンプト

```
@workspace /spec .github/spec/generated/day2-ec-customers-table.md と day2-ec-sales-table.md を参照し、backend/src/main/resources/schema.sql に以下のテーブルを追加してください：

1. customer テーブル（既存の products テーブルの後に追加）
2. sale テーブル（customer テーブルの後に追加）

テーブル作成順序を守ること：
- products（既存）
- customer
- sale（customer_id と product_id の外部キーを参照）

サンプルデータも追加すること：
- customer: 5件
- sale: 10件
```

#### 確認
```bash
# schema.sql を確認
cat backend/src/main/resources/schema.sql | grep "CREATE TABLE"
```

期待される出力:
```
CREATE TABLE IF NOT EXISTS products
CREATE TABLE IF NOT EXISTS customer
CREATE TABLE IF NOT EXISTS sale
```

---

### ステップ2: データベースに反映

```bash
psql -U postgres -d ecsite -f backend/src/main/resources/schema.sql
```

✅ `customer` と `sale` テーブルが作成されたことを確認：

```bash
psql -U postgres -d ecsite -c "\dt"
```

---

### ステップ3: Customer Entity 実装

#### GitHub Copilot プロンプト

```
@workspace /spec .github/spec/generated/day2-ec-customer-api-spec.md を参照し、Customer エンティティを作成してください。

ファイル: backend/src/main/java/com/example/ecsite/entity/Customer.java
フィールド: id, name, email, password, phone, createdAt
アノテーション: @Entity, @Table(name="customer"), @Id, @GeneratedValue(strategy = GenerationType.IDENTITY)
```

---

### ステップ4: Sale Entity 実装

#### GitHub Copilot プロンプト

```
@workspace /spec .github/spec/generated/day2-ec-sales-api-spec.md を参照し、Sale エンティティを作成してください。

ファイル: backend/src/main/java/com/example/ecsite/entity/Sale.java
フィールド: id, customer (ManyToOne), product (ManyToOne), quantity, saleDate, totalAmount
外部キー: customer_id → customer.id, product_id → products.id
```

---

### ステップ5: SaleDTO 実装（重要）

**問題**: Sale エンティティは `customerId` と `productId` だけで、顧客名・商品名が含まれていません。

**解決策**: DTO パターンを使用します。

#### GitHub Copilot プロンプト

```
@workspace /spec .github/spec/generated/day2-ec-sales-api-spec.md を参照し、SaleDTO を作成してください。

ファイル: backend/src/main/java/com/example/ecsite/dto/SaleDTO.java
フィールド: id, customerName, productName, quantity, totalAmount, saleDate

このDTOは、Sale エンティティを Customer と Product で JOIN した結果を格納します。
```

---

### ステップ6: Repository 実装

#### CustomerRepository

```
@workspace /spec .github/spec/generated/day2-ec-customer-api-spec.md を参照し、CustomerRepository を作成してください。

ファイル: backend/src/main/java/com/example/ecsite/repository/CustomerRepository.java
継承: JpaRepository<Customer, Long>
カスタムメソッド: Optional<Customer> findByEmail(String email);
```

#### SaleRepository

```
@workspace /spec .github/spec/generated/day2-ec-sales-api-spec.md を参照し、SaleRepository を作成してください。

ファイル: backend/src/main/java/com/example/ecsite/repository/SaleRepository.java
継承: JpaRepository<Sale, Long>
```

---

### ステップ7: Service 実装

#### CustomerService

```
@workspace /spec .github/spec/generated/day2-ec-customer-api-spec.md を参照し、CustomerService を作成してください。

ファイル: backend/src/main/java/com/example/ecsite/service/CustomerService.java
メソッド:
- List<Customer> getAllCustomers()
- Optional<Customer> getCustomerById(Long id)
- Customer createCustomer(Customer customer)
- Customer updateCustomer(Long id, Customer customer)

アノテーション: @Service, @Transactional
```

#### SaleService（DTO 変換含む）

```
@workspace /spec .github/spec/generated/day2-ec-sales-api-spec.md を参照し、SaleService を作成してください。

ファイル: backend/src/main/java/com/example/ecsite/service/SaleService.java
メソッド:
- List<SaleDTO> getAllSales() ← Sale を Customer と Product で JOIN し、SaleDTO に変換
- Optional<SaleDTO> getSaleById(Long id)
- Sale createSale(Sale sale)

アノテーション: @Service, @Transactional

getAllSales() の実装:
1. saleRepository.findAll() で全件取得
2. 各 Sale を SaleDTO に変換（customer.name と product.name を取得）
3. List<SaleDTO> を返す
```

---

### ステップ8: Controller 実装

#### CustomerController

```
@workspace /spec .github/spec/generated/day2-ec-customer-api-spec.md を参照し、CustomerController を作成してください。

ファイル: backend/src/main/java/com/example/ecsite/controller/CustomerController.java
エンドポイント:
- GET /api/customers
- GET /api/customers/{id}
- POST /api/customers
- PUT /api/customers/{id}

アノテーション: @RestController, @RequestMapping("/api/customers"), @CrossOrigin(origins = "http://localhost:3000")
```

#### SaleController

```
@workspace /spec .github/spec/generated/day2-ec-sales-api-spec.md を参照し、SaleController を作成してください。

ファイル: backend/src/main/java/com/example/ecsite/controller/SaleController.java
エンドポイント:
- GET /api/sales ← List<SaleDTO> を返す
- GET /api/sales/{id} ← SaleDTO を返す
- POST /api/sales

アノテーション: @RestController, @RequestMapping("/api/sales"), @CrossOrigin(origins = "http://localhost:3000")
```

---

### ステップ9: Auth API 実装

#### AuthController

```
@workspace /spec .github/spec/generated/day2-ec-customer-api-spec.md を参照し、AuthController を作成してください。

ファイル: backend/src/main/java/com/example/ecsite/controller/AuthController.java
エンドポイント: POST /api/auth/login
リクエストボディ: {"email": "string", "password": "string"}
レスポンス（成功）: Customer 情報（id, name, email, phone）
レスポンス（失敗）: 401 Unauthorized {"error": "Invalid email or password"}

実装:
1. CustomerRepository.findByEmail(email) で顧客を取得
2. password が一致するか確認（平文比較）
3. 一致すれば Customer を返す
4. 不一致なら 401 エラー
```

---

## 🚀 動作確認

### 1. Backend 起動
```bash
cd backend
./mvnw spring-boot:run
```

### 2. Customer API テスト
```bash
# 全件取得
curl http://localhost:8080/api/customers

# 詳細取得
curl http://localhost:8080/api/customers/1

# 新規登録
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "新規顧客",
    "email": "new@example.com",
    "password": "password123",
    "phone": "090-9999-8888"
  }'
```

### 3. Sales API テスト
```bash
# 全件取得（顧客名・商品名含む）
curl http://localhost:8080/api/sales

# 期待されるレスポンス
[
  {
    "id": 1,
    "customerName": "田中太郎",
    "productName": "ノートPC",
    "quantity": 2,
    "totalAmount": 240.00,
    "saleDate": "2026-01-15T10:30:00"
  }
]
```

### 4. Auth API テスト
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"tanaka@example.com","password":"password123"}'
```

---

## ❌ トラブルシューティング

### エラー1: `relation "customer" does not exist`
**原因**: customer テーブルが作成されていない

**解決策**:
```bash
psql -U postgres -d ecsite -f backend/src/main/resources/schema.sql
```

### エラー2: `@workspace が効かない`
**原因**: .md ファイルがエディタで開かれていない

**解決策**:
1. `.github/spec/generated/day2-ec-customer-api-spec.md` をエディタで開く
2. Copilot Chat を再度開く

---

## 📚 参考資料
- `.github/spec/reference/day2-ec-customer-api-spec.md`（完成版）
- `.github/spec/reference/day2-ec-sales-api-spec.md`（完成版）
- `.github/spec/reference/backend-requirements-customer-sales.txt`

---

## ✅ 次のステップ
Backend実装が完了したら、以下に進みます：

1. ~~Day2: Backend実装~~ ✅ 完了
2. **Day2: Frontend実装** ← 次はこれ

---

🎉 Backend実装完了です！
