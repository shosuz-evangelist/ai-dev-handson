# spec: customer-management（顧客管理）

> これは到達点の一例です。同じ受け入れ基準を満たしていれば、コードの細部は違って構いません。

## 何を作るか

顧客の登録・取得・一覧・更新・削除を行う REST API。

## 受け入れ基準

1. `POST /api/customers` で顧客を登録でき、201 と作成された顧客を返す
2. `email` が既存と重複する場合は **409 Conflict** を返す
3. `GET /api/customers/{id}` は存在すれば 200、存在しなければ **404 Not Found**
4. `PUT /api/customers/{id}` は全項目を更新する。存在しなければ 404
5. `DELETE /api/customers/{id}` は**論理削除**する（`deleted_at` を立てる）。204 を返す
6. `GET /api/customers` の一覧は**削除済みを含めない**
7. 入力検証: `name` は必須で 100 文字以内、`email` は必須で形式が正しいこと。違反は **400 Bad Request**

## Clarifications（/speckit-clarify で確定した内容）

- Q: email 重複時のレスポンスは？ → **409 Conflict**
- Q: 存在しない id の取得・更新は？ → **404 Not Found**
- Q: 削除は論理か物理か → **論理削除**。一覧では既定で除外する
- Q: 更新は部分更新か全項目か → **PUT で全項目更新**
- Q: 認可は？ → この演習では扱わない（認証なしで実行できる）

## 対象外

- 認証・認可
- ページング（一覧は全件返す）
