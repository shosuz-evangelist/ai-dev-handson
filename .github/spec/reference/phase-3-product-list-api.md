# Phase 3: 商品一覧API

## 目的

REST APIとJSON レスポンスの理解、CORS設定、Controller-Serviceパターンの実装を習得する。

## 成果物

- `GET /api/products` エンドポイントの実装
- `ProductController.java` の作成
- `ProductService.java` の作成
- CORS設定（@CrossOrigin）

## 前提条件

- Phase 2（データベース接続）が完了していること
- productsテーブルに20件のデータが投入済みであること

## API 仕様

### エンドポイント

```bash
GET /api/products
```bash

### レスポンス例

```json
[
  {
    "id": 1,
    "name": "ボールペン（黒）",
    "price": 120,
    "imageUrl": "https://example.com/pen-black.jpg"
  },
  {
    "id": 2,
    "name": "ボールペン（赤）",
    "price": 120,
    "imageUrl": "https://example.com/pen-red.jpg"
  },
  ...
]
```bash

### ステータスコード

- 200 OK: 正常応答

### CORS設定

- Origin: `http://localhost:3000` を許可

## 実装手順

### 1. 仕様書を確認

GitHub Copilot Chatで以下を実行:

```bash
@workspace /spec .github/spec/phase-3-product-list-api.md の商品一覧取得 API の仕様をもとに実装計画を作成して
```bash

### 2. ProductController の作成

GitHub Copilot Chatで以下を実行:

```bash
実装計画をもとに ProductController.java と ProductService.java の雛形を生成して
```bash

`backend/src/main/java/com/example/ecsite/controller/ProductController.java`:

```java
package com.example.ecsite.controller;

import com.example.ecsite.entity.Product;
import com.example.ecsite.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {
    
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }
}
```bash

### 3. ProductService の作成

`backend/src/main/java/com/example/ecsite/service/ProductService.java`:

```java
package com.example.ecsite.service;

import com.example.ecsite.entity.Product;
import com.example.ecsite.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService {
    
    private final ProductRepository productRepository;
    
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
```bash

### 4. アプリケーションの起動

```bash
cd backend
./mvnw spring-boot:run
```bash

### 5. Postman で動作確認

- Method: GET
- URL: `http://localhost:8080/api/products`
- Expected: 20件の商品データが返却される

## 3層アーキテクチャ

```bash
Controller (プレゼンテーション層)
  ↓ HTTPリクエスト/レスポンス
Service (ビジネスロジック層)
  ↓ データ操作
Repository (データアクセス層)
  ↓ SQL
Database (PostgreSQL)
```bash

## GitHub Copilot プロンプト例

```bash
@workspace /spec .github/spec/phase-3-product-list-api.md の商品一覧取得APIの仕様をもとに実装計画を作成して
```bash

```bash
この実装計画をもとに ProductController.java と ProductService.java の雛形を生成して
```bash

```bash
@CrossOrigin の origins に localhost:3000 を指定する理由を説明して
```bash

## よくあるエラー

### エラー1: CORSでブロック

**原因:** `@CrossOrigin` が設定されていない

**対策:**

```java
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")  // 追加
public class ProductController {
    // ...
}
```bash

**ブラウザのエラーメッセージ例:**

```bash
Access to fetch at 'http://localhost:8080/api/products' from origin 'http://localhost:3000' has been blocked by CORS policy
```bash

### エラー2: ServiceがDIされない

**原因:** `@Service` アノテーションが付いていない、またはコンポーネントスキャン対象外

**対策:**

- `@Service` を付与
- パッケージ構造を確認（`@SpringBootApplication` と同じまたはサブパッケージ）

### エラー3: 空の配列 [] が返る

**原因:** データベースにデータが投入されていない

**対策:**

- pgAdminで確認: `SELECT * FROM products;`
- schema.sqlが実行されているか確認
- `spring.sql.init.mode=always` が設定されているか確認

## FAQ

### Q: Controllerに直接Repository注入は？

**A:** 技術的には可能ですが非推奨です。Service層を挟むことでビジネスロジックの再利用性とテストのしやすさが向上します。

### Q: レスポンス例は？

**A:** 仕様書にJSON形式のレスポンス例を記載すると、GitHub Copilotによるコード生成の精度が大幅に向上します。

### Q: @Transactional(readOnly = true) の意味は？

**A:** 読み取り専用トランザクションとして実行することで、パフォーマンスが向上し、誤ってデータを更新するのを防ぎます。

### Q: CORS設定を全体に適用するには？

**A:** `WebMvcConfigurer` を実装したConfiguration クラスで設定できます:

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000");
    }
}
```bash

## 所要時間

約45分

## 次のステップ

Phase 4: 商品詳細APIの実装に進む
