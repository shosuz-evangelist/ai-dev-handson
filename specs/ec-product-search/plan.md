# plan: ec-product-search

## 設計

- `ProductRepository` に検索メソッドを足す。JPQL で `keyword` と `category` の有無を吸収する
  - `keyword` は `LOWER(name) LIKE LOWER(CONCAT('%', :keyword, '%'))`
  - どちらも `:param IS NULL` で「指定なし」を表す
- `ProductService#search` … 引数の既定値を決めてリポジトリに渡す
- `ProductController` に `GET /api/products/search` を足す
- `ProductSearchResponse`（DTO）… `items` と `totalCount` と `page` と `size` を返す

## 規約に従う点（constitution.md より）

- REST・複数形エンドポイント・DTO と Entity の分離
- スキーマは変更しない（`category` と `stock` は V1 で作成済み、索引も作成済み）

## ページング

- `Pageable` を使う。既定は `page=0` / `size=20` / `name` の昇順
