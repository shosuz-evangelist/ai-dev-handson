# 商品API仕様書 (Product API Specification)

## 概要
EC サイトの商品管理機能を提供する REST API の仕様書です。
商品の一覧取得、詳細取得、新規登録の3つのエンドポイントを定義します。

---

## エンドポイント一覧

### 1. 商品一覧取得 API

**エンドポイント**: `GET /api/products`

**目的**: 登録されている全商品の一覧を取得する

**リクエスト**:
- パラメータ: なし
- ヘッダー: なし

**レスポンス**:
- ステータスコード: `200 OK`
- Content-Type: `application/json`

**レスポンスボディ**:
```json
[
  {
    "id": 1,
    "name": "ボールペン",
    "price": 120,
    "imageUrl": "https://raw.githubusercontent.com/shosuz-evangelist/ai-driven-dev-training-speckit/main/images/pen.jpg"
  },
  {
    "id": 2,
    "name": "ノート",
    "price": 200,
    "imageUrl": "https://raw.githubusercontent.com/shosuz-evangelist/ai-driven-dev-training-speckit/main/images/notebook.jpg"
  }
]
```

**エラーレスポンス**:
- なし（空配列を返す）

---

### 2. 商品詳細取得 API

**エンドポイント**: `GET /api/products/{id}`

**目的**: 指定されたIDの商品の詳細情報を取得する

**リクエスト**:
- パスパラメータ:
  - `id` (Long): 商品ID
- ヘッダー: なし

**レスポンス（正常時）**:
- ステータスコード: `200 OK`
- Content-Type: `application/json`

**レスポンスボディ**:
```json
{
  "id": 1,
  "name": "ボールペン",
  "price": 120,
  "imageUrl": "https://raw.githubusercontent.com/shosuz-evangelist/ai-driven-dev-training-speckit/main/images/pen.jpg"
}
```

**エラーレスポンス**:
- ステータスコード: `404 Not Found`
- Content-Type: `application/json`

**エラーレスポンスボディ**:
```json
{
  "error": "Product not found",
  "message": "商品ID {id} が見つかりません"
}
```

---

### 3. 商品登録 API

**エンドポイント**: `POST /api/products`

**目的**: 新しい商品を登録する

**リクエスト**:
- ヘッダー:
  - `Content-Type: application/json`

**リクエストボディ**:
```json
{
  "name": "蛍光ペンセット",
  "price": 300,
  "imageUrl": "https://raw.githubusercontent.com/shosuz-evangelist/ai-driven-dev-training-speckit/main/images/highlighter_set.jpg"
}
```

**バリデーションルール**:
- `name`: 必須、1文字以上255文字以内
- `price`: 必須、0以上の整数
- `imageUrl`: 必須、有効なURL形式

**レスポンス（正常時）**:
- ステータスコード: `201 Created`
- Content-Type: `application/json`

**レスポンスボディ**:
```json
{
  "id": 21,
  "name": "蛍光ペンセット",
  "price": 300,
  "imageUrl": "https://raw.githubusercontent.com/shosuz-evangelist/ai-driven-dev-training-speckit/main/images/highlighter_set.jpg"
}
```

**エラーレスポンス**:
- ステータスコード: `400 Bad Request`
- Content-Type: `application/json`

**エラーレスポンスボディ**:
```json
{
  "error": "Validation Error",
  "message": "バリデーションエラー",
  "details": [
    {
      "field": "name",
      "message": "商品名は必須です"
    },
    {
      "field": "price",
      "message": "価格は0以上である必要があります"
    }
  ]
}
```

---

## データモデル

### Product (商品)

| フィールド名 | 型 | 必須 | 説明 |
|------------|------|------|------|
| id | Long | ○ | 商品ID（自動採番） |
| name | String | ○ | 商品名（最大255文字） |
| price | Integer | ○ | 価格（円） |
| imageUrl | String | ○ | 商品画像URL |

---

## 実装時の注意事項

### Day 1 実装済み内容
- `GET /api/products` - 商品一覧取得API（Phase 3で実装済み）

### Day 2 実装対象
- `GET /api/products/{id}` - 商品詳細取得API（Phase 4で実装）
- `POST /api/products` - 商品登録API（Phase 5で実装）

### エラーハンドリング
- 商品が見つからない場合は `404 Not Found` を返す
- バリデーションエラーは `400 Bad Request` を返す
- サーバーエラーは `500 Internal Server Error` を返す

### セキュリティ
- 入力値のバリデーションを必ず実施
- SQLインジェクション対策（JPA使用により自動対応）
- XSS対策（エスケープ処理）

---

## テスト

### 正常系テストケース
1. 商品一覧取得: 20件のデータが返ること
2. 商品詳細取得: ID=1の商品情報が正しく返ること
3. 商品登録: 新規商品が正常に登録されること

### 異常系テストケース
1. 存在しないID指定: 404エラーが返ること
2. 不正なリクエストボディ: 400エラーが返ること
3. 必須項目の欠落: バリデーションエラーが返ること

---

## 参考情報

### 関連ファイル
- データベーススキーマ: `backend/src/main/resources/schema.sql`
- エンティティクラス: `backend/src/main/java/com/example/ecsite/entity/Product.java`
- リポジトリ: `backend/src/main/java/com/example/ecsite/repository/ProductRepository.java`

### 商品画像一覧
画像URLは以下のGitHubリポジトリから取得:
https://github.com/shosuz-evangelist/ai-driven-dev-training-speckit/tree/main/images

20種類の文房具画像が用意されています（pen.jpg, notebook.jpg, eraser.jpg など）
