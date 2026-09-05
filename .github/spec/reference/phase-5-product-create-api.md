# Phase 5: 商品登録API

## 目的

POST リクエストとリクエストボディの処理、バリデーション観点の習得、201 Createdステータスの理解。

## 成果物

- `POST /api/products` エンドポイントの実装
- `@RequestBody` による JSON 受け取り
- 201 Created ステータスコードの返却

## 前提条件

- Phase 4（商品詳細API）が完了していること

## API 仕様

### エンドポイント

```bash
POST /api/products
```bash

### リクエストボディ

```json
{
  "name": "新商品テスト",
  "price": 500,
  "imageUrl": "https://example.com/new-product.jpg"
}
```bash

### レスポンス例（正常系）

```json
{
  "id": 21,
  "name": "新商品テスト",
  "price": 500,
  "imageUrl": "https://example.com/new-product.jpg"
}
```bash

### ステータスコード

- 201 Created: 正常に作成された
- 400 Bad Request: リクエストボディが不正

## 実装手順

### 1. 仕様書を確認

GitHub Copilot Chatで以下を実行:

```bash
@workspace /spec .github/spec/phase-5-product-create-api.md の商品登録 API の仕様をもとに実装計画を作成して
```bash

### 2. ProductController に createProduct() を追加

GitHub Copilot Chatで以下を実行:

```bash
この計画に従い、ProductController に商品を登録し 201 Createdを返すメソッドを追加して
```bash

`ProductController.java` に追加:

```java
@PostMapping("/products")
public ResponseEntity<Product> createProduct(@RequestBody Product product) {
    Product created = productService.createProduct(product);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
}
```bash

### 3. ProductService に createProduct() を追加

`ProductService.java` に追加:

```java
@Transactional
public Product createProduct(Product product) {
    return productRepository.save(product);
}
```bash

### 4. Postman で動作確認

**テスト:**

- Method: POST
- URL: `http://localhost:8080/api/products`
- Headers: `Content-Type: application/json`
- Body (raw JSON):

```json
{
  "name": "テスト商品",
  "price": 999,
  "imageUrl": "https://example.com/test.jpg"
}
```bash

- Expected: 201 Created + 作成された商品データ

### 5. データベースで確認

pgAdminで以下を実行:

```sql
SELECT * FROM products ORDER BY id DESC LIMIT 1;
```bash

## GitHub Copilot プロンプト例

```bash
@workspace /spec 商品登録 API の仕様に基づき、実装計画とコードを生成して
```bash

```bash
@Valid と @NotNull アノテーションはどのフィールドに適用すべき？コード例も示して
```bash

```bash
ResponseEntity を使って HTTP 201 Created を返す実装方法は？
```bash

## よくあるエラー

### エラー1: @RequestBody 忘れ

**原因:** アノテーションがないと `null` として受け入れられる

```java
public ResponseEntity<Product> createProduct(Product product) {  // ❌
```bash

**対策:** 必ず `@RequestBody` を付与

```java
public ResponseEntity<Product> createProduct(@RequestBody Product product) {  // ✅
```bash

### エラー2: Content-Type 未指定

**原因:** リクエストヘッダーに `Content-Type: application/json` が設定されていない

**対策:** Postmanのヘッダーで以下を設定:

```bash
Content-Type: application/json
```bash

### エラー3: ID の手動設定

**原因:** リクエストボディでIDを指定するとシーケンスと競合する可能性

**対策:** IDは自動採番に任せる（リクエストボディでIDは指定しない）

## FAQ

### Q: ID は手動設定？

**A:** 自動採番に任せるのが基本です。手動設定すると DB のシーケンスと競合し、一意制約エラーの原因になります。

### Q: バリデーションは？

**A:** 今回は時間の都合で省略しますが、自習課題（Day1→Day2間）で `@Valid` や `@NotNull` 等を追加して実装してください。

### Q: 入力チェックの実装例は？

**A:** Bean Validation を使用:

```java
public class Product {
    @NotNull(message = "商品名は必須です")
    @Size(min = 1, max = 255)
    private String name;
    
    @NotNull
    @Min(0)
    private Integer price;
    
    @NotNull
    @Pattern(regexp = "^https?://.*")
    private String imageUrl;
}

// Controller
@PostMapping("/products")
public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
    // ...
}
```bash

## 所要時間

約30分

## 次のステップ

Phase 6: カート機能の設計（座学）に進む
