# plan: customer-management

## 設計

- `Customer`（Entity）… `id / name / email / phone / deletedAt / createdAt / updatedAt`
- `CustomerRepository`（Spring Data JPA）… `findByDeletedAtIsNull` / `existsByEmailAndDeletedAtIsNull`
- `CustomerService` … 業務ルール（重複判定・論理削除）
- `CustomerController` … `/api/customers`
- `CustomerRequest` / `CustomerResponse`（DTO）… Entity を直接返さない
- `DuplicateEmailException` + `@RestControllerAdvice` … 409 への変換

## 規約に従う点（constitution.md より）

- REST・複数形エンドポイント・DTO と Entity の分離・`@Valid` での入力検証
- スキーマは既存の Flyway マイグレーションのまま（`customers` は V1 で作成済み）

## エラー対応

| 状況 | HTTP |
|---|---|
| email 重複 | 409 |
| id が存在しない | 404 |
| 入力検証エラー | 400 |
