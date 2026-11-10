# tasks: ec-product-search

- [x] T1 `ProductRepository` に検索メソッドを足す（keyword / category / 在庫の有無）
- [x] T2 `ProductSearchResponse` を作る（items・totalCount・page・size）
- [x] T3 `ProductService#search` を作る（既定値の決定）
- [x] T4 `ProductController` に `GET /api/products/search` を足す
- [x] T5 受け入れ基準をテストにする（サービス・コントローラ）

※ T5 のテストのタスクは、`/speckit-tasks` に「受け入れ基準のテストのタスクも作る」と指示したときに作られます。
※ 演習2 では Issue 化を省きます。github 拡張は既にある Issue の題の T 番号で重複を判定するので、演習1 と T 番号が重なると Issue が作られません。
