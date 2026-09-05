# Phase 1: Hello World API

## 目的

Spring Boot の最小構成 API を理解し、Spec Kit を活用した開発フローを習得する。

## 成果物

- `GET /api/hello` エンドポイントで "Hello World" を返すAPI
- `HelloController.java` クラスの作成

## 前提条件

- Phase 0（環境確認）が完了していること
- Java 21, Spring Boot 3.x がセットアップ済み

## API 仕様

### エンドポイント

```bash
GET /api/hello
```bash

### レスポンス

```json
{
  "message": "Hello World"
}
```bash

### ステータスコード

- 200 OK: 正常応答

## 実装手順

### 1. 仕様書を確認

この仕様書（`.github/spec/phase-1-hello-api.md`）を開き、どのようなAPIを作るのか確認する。

### 2. GitHub Copilot で実装計画を生成

GitHub Copilot Chat で以下のプロンプトを実行:

```bash
@workspace /spec .github/spec/phase-1-hello-api.md の仕様を要約して
```bash

### 3. 実装計画に従ってコードを生成

```bash
この実装計画に従い、HelloController.javaを生成して
```bash

### 4. HelloController.java の作成

`backend/src/main/java/com/example/ecsite/controller/HelloController.java` を作成:

```java
package com.example.ecsite.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hello")
    public Map<String, String> hello() {
        return Map.of("message", "Hello World");
    }
}
```bash

### 5. アプリケーションの起動

```bash
cd backend
./mvnw spring-boot:run
```bash

### 6. 動作確認

ブラウザまたはPostmanで以下にアクセス:

```bash
http://localhost:8080/api/hello
```bash

**期待される出力:**

```json
{
  "message": "Hello World"
}
```bash

## GitHub Copilot プロンプト例

### Spec Kit ワークフロー

1. **Specify（仕様書確認）**

```bash
@workspace /spec .github/spec/phase-1-hello-api.md の仕様を要約して
```bash

1. **Plan（実装計画生成）**

```bash
この仕様をもとに実装計画を作成して
```bash

1. **Task（タスク分解）**

```bash
この実装計画をもとに、具体的なタスクに分解して
```bash

1. **Code（コード実装）**

```bash
計画に従い HelloController.java を生成して
```bash

### その他の有用なプロンプト

```bash
@RestController と @Controller の違いを初心者向けに解説して
```bash

```bash
@GetMapping の役割を説明して
```bash

## よくあるエラー

### エラー1: 404 エラー（ページが見つかりません）

**原因:** `@RestController` アノテーションが付いていない

**対策:**

```java
@RestController  // このアノテーションを追加
@RequestMapping("/api")
public class HelloController {
    // ...
}
```bash

### エラー2: ポート競合（起動失敗）

**原因:** ポート8080が既に使用されている

**対策:** `application.properties` でポートを変更

```properties
server.port=8081
```bash

### エラー3: パッケージ構造の誤り

**原因:** Controllerが `@SpringBootApplication` と同じパッケージまたはサブパッケージにない

**対策:** パッケージ構造を確認:

```bash
com.example.ecsite
├── EcSiteApplication.java
└── controller
    └── HelloController.java
```bash

## FAQ

### Q: @workspace /spec は毎回必要？

**A:** はい、仕様書を更新した際や、コンテキストが変わった際には `@workspace /spec` コマンドで仕様書を再読み込みすることが必須です。これによりAIが最新の仕様を正しく理解します。

### Q: 単体テストは？

**A:** 本セクションでは実装を優先するため省略します。Day1からDay2の間の自習課題として、JUnitを使用した単体テストの追加を推奨しています。

### Q: JSON以外のレスポンス形式は？

**A:** `@RestController` は自動的にJSONに変換します。XMLやプレーンテキストが必要な場合は `produces` 属性で指定します。

## 所要時間

約45分

## 次のステップ

Phase 2: データベース接続の実装に進む
