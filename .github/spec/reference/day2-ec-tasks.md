# Day2 EC Tasks (Implementation) — Customer / Sales 追加

このファイルは「受講者が迷わず実装を完了できる」ための *実装順* と *確認手順* をまとめたタスクリストです。

---

## 0. 前提（Day2データモデルの要点）
- データモデル: `customers` / `sales` / `products`
- `sales.total` は **quantity × price**
- Sales の単価フィールド名は **unit_price ではなく `price` に統一**（スライド準拠）

---

## 1. DB（スキーマ＆初期データ）

### 1.1 テーブル追加
- [ ] `customers` テーブルを追加
- [ ] `sales` テーブルを追加

### 1.2 制約
- [ ] `customers.email` に UNIQUE 制約（ある場合）
- [ ] `sales.customer_id` は customers への外部キー
- [ ] `sales.product_id` は products への外部キー
- [ ] `sales.quantity` は 1 以上（CHECK など）
- [ ] `sales.price` は 0 以上（CHECK など）

### 1.3 `total` の扱い
- [ ] `total` をDBに持つなら、保存時に quantity×price を計算して格納
- [ ] `total` を返却時に計算するなら、レスポンスDTOで算出

> 演習の目的上は「どちらでも可」ですが、受講者の理解が簡単なのは **保存時に計算してDBに保持**です。

---

## 2. Customer API（最小）

### 2.1 実装範囲（最小）
- [ ] `POST /api/customers`（顧客作成）
- [ ] `GET /api/customers`（一覧）
- [ ] `GET /api/customers/{id}`（詳細）

### 2.2 レイヤ構成（Spring Boot）
- [ ] Entity / Repository / Service / Controller を作る
- [ ] Request/Response DTO を作る
- [ ] Bean Validation（例: `@NotNull`, `@Email`, `@Size`）
- [ ] 例外処理（400/404/409 を返す）

### 2.3 テスト
- [ ] Controller テスト（正常系 + バリデーションエラー）
- [ ] Service テスト（重複/存在チェック等）

---

## 3. Sales API（最小 + 合計確認ができること）

### 3.1 実装範囲（最小）
- [ ] `POST /api/sales`（売上登録）
- [ ] `GET /api/sales`（一覧。合計totalが確認できる）

### 3.2 仕様ポイント
- [ ] `price` は products の単価をコピーして保存（または指定で受けて保存）
- [ ] `total = quantity * price` を必ず満たす

### 3.3 レイヤ構成
- [ ] Entity / Repository / Service / Controller
- [ ] DTO + Validation
- [ ] 例外（customerId/productId 不正 → 404、quantity不正 → 400）

### 3.4 テスト
- [ ] total の計算が正しいこと
- [ ] 不正ID/不正quantity のエラー

---

## 4. スモークテスト（動作確認：これを通せば演習完了）

### 4.1 例（curl）
1) 顧客作成
```bash
curl -X POST http://localhost:8080/api/customers \
  -H 'Content-Type: application/json' \
  -d '{"name":"Taro","email":"taro@example.com"}'
```

2) 売上登録（例）
```bash
curl -X POST http://localhost:8080/api/sales \
  -H 'Content-Type: application/json' \
  -d '{"customerId":1,"productId":1,"quantity":2}'
```

3) 売上一覧（total確認）
```bash
curl http://localhost:8080/api/sales
```

### 4.2 期待する確認
- [ ] `quantity=2` かつ `price` が products の単価なら `total=2*price`
- [ ] 仕様書（day2-ec-sales-api-spec.md）とレスポンス項目名が一致している

---

## 5. 最終チェック（講師が見るポイント）
- [ ] フィールド名が `unit_price` ではなく **`price`** で統一されている
- [ ] total が必ず quantity×price
- [ ] 例外が 400/404/409 で整理されている
- [ ] 最小のテストが付いている
