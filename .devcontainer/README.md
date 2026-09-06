# .devcontainer

Codespaces で開いたときの環境です。手元に何も入れなくても、そのまま演習に入れます。

| | |
|---|---|
| Java | 21（`pom.xml` の指定に合わせています） |
| Maven | 同梱（`backend/mvnw` も使えます） |
| PostgreSQL | 15。`localhost:5432` / DB `ecsite` / ユーザ `ecsite` / パスワード `ecsite` |
| 拡張 | GitHub Copilot、Copilot Chat、Java 一式 |

作成後に `cd backend && ./mvnw test` が 1 回走ります。**最初から緑**になっていることを確認してから演習に入ってください。

アプリを起動する場合は次のとおりです。スキーマとサンプルデータは Flyway が投入します。

```bash
cd backend
./mvnw spring-boot:run
```
