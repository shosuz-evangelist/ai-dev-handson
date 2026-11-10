# tasks: customer-management

- [x] T1 `Customer` エンティティを作る（論理削除の列を含む）
- [x] T2 `CustomerRepository` を作る（削除済みを除外する検索）
- [x] T3 `CustomerRequest` / `CustomerResponse` を作る
- [x] T4 `DuplicateEmailException` と `@RestControllerAdvice` を作る
- [x] T5 `CustomerService` を作る（重複判定・論理削除）
- [x] T6 `CustomerController` を作る（登録・取得・一覧・更新・削除）
- [x] T7 受け入れ基準をテストにする（サービス・コントローラ）

※ T7 のテストのタスクは、`/speckit-tasks` に「受け入れ基準のテストのタスクも作る」と指示したときに作られます（Spec Kit は指示しないとテストのタスクを作りません）。
※ github 拡張の `/speckit-github-taskstoissues` を使うと、この 1 行が 1 Issue になります（題は `T001: …` の形で、実際の番号の振り方はこの例と違うことがあります）。
