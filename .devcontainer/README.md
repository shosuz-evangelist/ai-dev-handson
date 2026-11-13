# .devcontainer

Codespaces で開いたときの環境です。手元に何も入れなくても、そのまま演習に入れます。

| | |
|---|---|
| Java | 21（`pom.xml` の指定に合わせています） |
| Maven | 同梱（`backend/mvnw` も使えます） |
| PostgreSQL | 15。`localhost:5432` / DB `ecsite` / ユーザ `ecsite` / パスワード `ecsite` |
| Python | Python 3 と PyYAML（Spec Kit v1.3.3 のスクリプトが、拡張の登録簿とテンプレートを読むのに使います） |
| 拡張 | GitHub Copilot、Copilot Chat、Java 一式 |

作成後に Python 3 と PyYAML が入り、`cd backend && ./mvnw test` が 1 回実行されます。**最初からテストが通っている**ことを確認してから演習に入ってください。

アプリを起動する場合は次のとおりです。スキーマとサンプルデータは Flyway が投入します。

```bash
cd backend
./mvnw spring-boot:run
```
