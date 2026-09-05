# Phase 4: 商品詳細API

## 目的

パスパラメータとエラーハンドリング（Optional、404 Not Found）を習得する。

## 成果物

- `GET /api/products/{id}` エンドポイントの実装
- ProductControllerへの詳細取得メソッド追加
- Optional クラスを使用した安全なエラーハンドリング

## 前提条件

- Phase 3（商品一覧API）が完了していること

## API 仕様

### エンドポイント

```bash
GET /api/products/{id}
```bash

### パスパラメータ

- `id`: 商品ID (Long型)

### レスポンス例（正常系）

```json
{
  "id": 1,
  "name": "ボールペン（黒）",
  "price": 120,
  "imageUrl": "https://example.com/pen-black.jpg"
}
```bash

### ステータスコード

- 200 OK: 正常応答
- 404 Not Found: 指定されたIDの商品が存在しない

## 実装手順

### 1. 仕様書を確認

GitHub Copilot Chatで以下を実行:

```bash
@workspace /spec .github/spec/phase-4-product-detail-api.md の仕様をもとに、商品詳細取得 API の実装計画を作成して
```bash

### 2. ProductController に getProductById() を追加

GitHub Copilot Chatで以下を実行:

```bash
計画に従い、存在しない ID の場合は 404 エラーを返す実装を行って
```bash

`ProductController.java` に追加:

```java
@GetMapping("/products/{id}")
public ResponseEntity<Product> getProductById(@PathVariable Long id) {
    return productService.getProductById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}
```bash

### 3. ProductService に getProductById() を追加

`ProductService.java` に追加:

```java
public Optional<Product> getProductById(Long id) {
    return productRepository.findById(id);
}
```bash

### 4. Postman で動作確認

**正常系テスト:**

- Method: GET
- URL: `http://localhost:8080/api/products/1`
- Expected: 200 OK + 商品詳細

**異常系テスト:**

- Method: GET
- URL: `http://localhost:8080/api/products/999`
- Expected: 404 Not Found

## GitHub Copilot プロンプト例

```bash
@workspace /spec 詳細 API の実装計画を作成して
```bash

```bash
ProductController に GET /api/products/{id} を追加。存在しない ID は 404 を返して
```bash

```bash
Optional を使った 404 エラーハンドリングのコード例を示して
```bash

```bash
@PathVariable と@RequestParam の違いを説明して
```bash

## よくあるエラー

### エラー1: 変数名とパスパラメータ名の不一致

**原因:**

```java
@GetMapping("/products/{id}")
public ResponseEntity<Product> getProductById(@PathVariable("id") Long productId) {
    // ...
}
```bash

**対策:** パス変数名を一致させる

```java
@GetMapping("/products/{id}")
public ResponseEntity<Product> getProductById(@PathVariable Long id) {
    // ...
}
```bash

### エラー2: Optional.get() の乱用

**原因:** 値がない場合に `NoSuchElementException` が発生

```java
Product product = productService.getProductById(id).get();  // ❌
```bash

**対策:** `orElseThrow()` を使用して明示的に例外をスロー

```java
Product product = productService.getProductById(id)
    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
```bash

## FAQ

### Q: エラーメッセージもカスタマイズできますか？

**A:** はい。`ResponseStatusException` の第二引数にメッセージを指定できます。

```java
new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found")
```bash

### Q: 404エラーのテストはどうすれば良いですか？

**A:** 今回は実装時間の都合上省略しますが、自習課題（Day1→Day2間）に含めます。JUnit等を用いて異常系のテストケースとして実装する練習を行います。

### Q: ResponseEntity の利点は？

**A:** HTTPステータスコード、ヘッダー、ボディを柔軟に制御できます。`@RestController` + 戻り値だけでは制御できない細かいレスポンス設定が可能です。

## 所要時間

約30分

## 次のステップ

Phase 5: 商品登録APIの実装に進む
