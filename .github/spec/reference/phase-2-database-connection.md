# Phase 2: データベース接続

## 目的

PostgreSQLとの接続を確立し、JPA（Java Persistence API）を使った基本的なCRUD操作を理解する。

## 成果物

- `Product.java` エンティティクラス
- `ProductRepository.java` リポジトリインターフェース
- `application.properties` でのDB接続設定
- `schema.sql` でのテーブル作成とサンプルデータ投入

## 前提条件

- Phase 1（Hello World API）が完了していること
- PostgreSQL 15がローカルで起動していること

## データベース仕様

### テーブル名: products

| カラム名 | 型 | 制約 | 説明 |
|---------|-----|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 商品ID |
| name | VARCHAR(255) | NOT NULL | 商品名 |
| price | INTEGER | NOT NULL | 価格（円） |
| image_url | VARCHAR(500) | NOT NULL | 画像URL |

### サンプルデータ

20件の文房具データを投入（例: ボールペン、ノート、消しゴムなど）

## 実装手順

### 1. 仕様書を確認

GitHub Copilot Chatで以下を実行:

```bash
@workspace /spec .github/spec/phase-2-database-connection.md の仕様をもとに実装計画を作成して
```bash

### 2. application.properties の設定

`backend/src/main/resources/application.properties` にDB接続情報を追加:

```properties
# PostgreSQL接続設定
spring.datasource.url=jdbc:postgresql://localhost:5432/ecsite
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA設定
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# スキーマ初期化
spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:schema.sql
```bash

### 3. Product エンティティの作成

GitHub Copilot Chatで以下を実行:

```bash
計画に従い、Productエンティティクラスを生成して。フィールドは id (Long, PK), name (String), price (Integer), imageUrl (String)
```bash

`backend/src/main/java/com/example/ecsite/entity/Product.java`:

```java
package com.example.ecsite.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private Integer price;
    
    @Column(name = "image_url", nullable = false)
    private String imageUrl;
    
    // コンストラクタ
    public Product() {}
    
    public Product(String name, Integer price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }
    
    // Getter/Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Integer getPrice() { return price; }
    public void setPrice(Integer price) { this.price = price; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
```bash

### 4. ProductRepository の作成

GitHub Copilot Chatで以下を実行:

```bash
JpaRepository を継承する ProductRepository インターフェースを作成して
```bash

`backend/src/main/java/com/example/ecsite/repository/ProductRepository.java`:

```java
package com.example.ecsite.repository;

import com.example.ecsite.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // 基本的なCRUD操作はJpaRepositoryが提供
}
```bash

### 5. schema.sql の作成

`backend/src/main/resources/schema.sql`:

```sql
-- テーブルが存在する場合は削除
DROP TABLE IF EXISTS products CASCADE;

-- productsテーブル作成
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price INTEGER NOT NULL,
    image_url VARCHAR(500) NOT NULL
);

-- サンプルデータ投入（20件の文房具）
INSERT INTO products (name, price, image_url) VALUES
('ボールペン（黒）', 120, 'https://example.com/pen-black.jpg'),
('ボールペン（赤）', 120, 'https://example.com/pen-red.jpg'),
('ボールペン（青）', 120, 'https://example.com/pen-blue.jpg'),
('シャープペンシル', 300, 'https://example.com/mechanical-pencil.jpg'),
('消しゴム', 100, 'https://example.com/eraser.jpg'),
('ノート（A4）', 200, 'https://example.com/notebook-a4.jpg'),
('ノート（B5）', 180, 'https://example.com/notebook-b5.jpg'),
('付箋紙', 250, 'https://example.com/sticky-notes.jpg'),
('クリアファイル', 150, 'https://example.com/clear-file.jpg'),
('クリップ（大）', 80, 'https://example.com/clip-large.jpg'),
('クリップ（小）', 50, 'https://example.com/clip-small.jpg'),
('ホッチキス', 500, 'https://example.com/stapler.jpg'),
('ホッチキス針', 100, 'https://example.com/staples.jpg'),
('修正テープ', 300, 'https://example.com/correction-tape.jpg'),
('マーカー（黄）', 150, 'https://example.com/marker-yellow.jpg'),
('マーカー（ピンク）', 150, 'https://example.com/marker-pink.jpg'),
('定規（30cm）', 200, 'https://example.com/ruler-30cm.jpg'),
('カッター', 250, 'https://example.com/cutter.jpg'),
('のり（スティック）', 120, 'https://example.com/glue-stick.jpg'),
('セロハンテープ', 180, 'https://example.com/tape.jpg');
```bash

### 6. アプリケーションの起動

```bash
cd backend
./mvnw spring-boot:run
```bash

### 7. 起動ログで接続確認

ログに以下のようなSQLが表示されればOK:

```bash
Hibernate: DROP TABLE IF EXISTS products CASCADE
Hibernate: CREATE TABLE products (...)
Hibernate: INSERT INTO products ...
```bash

### 8. pgAdmin で確認

- Host: localhost
- Port: 5432
- Database: ecsite
- クエリ実行: `SELECT * FROM products;`
- 20件のデータが表示されることを確認

## GitHub Copilot プロンプト例

```bash
@workspace /spec .github/spec/phase-2-database-connection.md の仕様をもとに実装計画を作成して
```bash

```bash
Productエンティティクラスを作成。フィールドは id (Long, PK), name (String), price (Integer), imageUrl (String)
```bash

```bash
JpaRepository を継承する ProductRepository インターフェースを作成して
```bash

```bash
@Entity と @Table の違いをわかりやすく説明して
```bash

```bash
application.properties にDB接続設定を追加して
```bash

## よくあるエラー

### エラー1: @Id アノテーション未設定

**原因:** 主キーが定義されていない

**対策:**

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```bash

### エラー2: DB 未起動 / 接続エラー

**原因:** PostgreSQLが起動していない、または接続情報が間違っている

**対策:**

- PostgreSQLサービスの起動確認
- `application.properties`の接続情報確認
- データベース`ecsite`が作成されているか確認

```bash
psql -U postgres -c "CREATE DATABASE ecsite;"
```bash

### エラー3: ddl-auto=create で既存データ消失

**原因:** `spring.jpa.hibernate.ddl-auto=create` で起動のたびにテーブルが再作成される

**対策:** 本番環境では`none`または`validate`に設定

```properties
spring.jpa.hibernate.ddl-auto=none
```bash

## FAQ

### Q: ddl-auto はcreate で良い？

**A:** 本番環境では危険です。既存データが消える可能性があるため、`none` や `validate` に設定して手動でスキーマ管理を行うことを推奨します。

### Q: 画像は DB 格納？

**A:** いいえ、DB には URL のみ（例: GitHub raw URL）を保持します。画像データそのものを DB に格納するとパフォーマンス低下の原因になります。

### Q: JpaRepository の利点は？

**A:** 基本的なCRUD操作（`findAll()`, `findById()`, `save()`, `deleteById()`など）が自動的に提供され、コード量を削減できます。

## 所要時間

約45分

## 次のステップ

Phase 3: 商品一覧APIの実装に進む
