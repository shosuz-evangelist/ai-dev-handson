# AI 駆動開発 SDD トレーニング（初級）

仕様駆動で EC バックエンドを作るハンズオンの教材リポジトリです。
**環境構築はしません。** 初期状態から、そのまま本題に入れます。

## はじめかた（受講者の方へ）

1. GitHub にサインインします。
2. このページの右上の **Fork** を押して、自分のアカウントにコピーします。
   演習の後半で Issue に Copilot を割り当てるため、**自分のリポジトリである必要があります**。
3. 自分の fork の **Code** から **Codespaces** を作ります（ブラウザだけで開けます）。
   作れない場合は、HTTPS の URL を `git clone` して VS Code で開きます。
   手元で開く場合は、**JDK 21 以上**と **Python 3・PyYAML** を入れておきます（Spec Kit のスクリプトが Python 3 で拡張の登録簿とテンプレートを読みます）。
4. 自分の fork の **Settings → General → Features** で **Issues** を有効にします（fork は既定で無効です）。
5. `cd backend && ./mvnw -B test` を実行し、`Tests run: 5` が出ることを確かめます。
6. `python3 -c "import yaml"` を実行し、何も出なければ準備完了です（Codespaces では作成時に入ります）。

## 何が入っているか

| | 中身 |
|---|---|
| `backend/` | Spring Boot の EC バックエンド（Java 21 / PostgreSQL 15 / Flyway） |
| `.specify/` | Spec Kit v1.3.3。規約は `.specify/memory/constitution.md`。Issue 化の github 拡張は `.specify/extensions/github/` |
| `.github/skills/speckit-*/` | Spec Kit のコマンド（Agent Skills 形式・コア 9 個 + github 拡張 1 個） |
| `.github/hooks/hooks.json` | 強制層の例。既にあるマイグレーションの直接編集を止める |
| `AGENTS.md` | AI ツール共通の入口。規約を参照する |

`products` と `customers` のテーブルは最初から用意してあります。
**customers の API はまだありません。** それを作るのが演習1です。

## コマンドの打ち方

Spec Kit は v1.0 から、Copilot 向けの配布が **Agent Skills 形式**です（このリポジトリは v1.3.3）。
コマンド名は `.github/skills/` のディレクトリ名がそのまま使われるため、**ハイフン**です。

```
/speckit-specify        （旧: /speckit.specify）
/speckit-clarify
/speckit-github-taskstoissues   （github 拡張）
```

基本 6 = `constitution` / `specify` / `plan` / `tasks` / `implement` / `converge`
品質ゲート 3 = `clarify` / `checklist` / `analyze`

`taskstoissues` は v1.3.0 でコアから外れ、github 拡張の `/speckit-github-taskstoissues` になりました。
使うには、エージェントから GitHub MCP サーバ（`list_issues` と `issue_write`）を使える必要があります。

コマンドが出ないときや古いときは、Spec Kit v1.3.3 の CLI で次を実行します。

```bash
uv tool install specify-cli --from git+https://github.com/github/spec-kit.git@v1.3.3
specify integration upgrade copilot
specify extension add github
```

## 当日の流れ（8 ステップ）

演習1（顧客管理）で講師と一緒に一周し、演習2（商品検索）を自力で一周します。

1. 意図を一言から始める
2. `/speckit-specify` で spec.md を作る
3. `/speckit-clarify` で曖昧さを潰す
4. `/speckit-plan` と `/speckit-tasks`（受け入れ基準のテストのタスクも作る）→ `/speckit-github-taskstoissues` で Issue 化
5. specs/ を push してから、Issue を 1 本 Copilot に渡す
6. 残りは手元で `/speckit-implement` → `/speckit-converge`（残りが出たら implement に戻る）
7. テスト = 受け入れ基準 = 真
8. Hooks で機械的に強制する

演習2 では Issue 化を省き、手元で implement します（github 拡張は既にある Issue の題の T 番号で重複を判定するので、演習1 と T 番号が重なると Issue が作られません）。

## 始め方

```bash
git clone https://github.com/shosuz-evangelist/ai-dev-handson
cd ai-dev-handson/backend
./mvnw test          # 既存テストが通ることを確認
```

起動には PostgreSQL が要ります。`backend/src/main/resources/application.properties` の
接続先を自分の環境に合わせてください。スキーマとサンプルデータは Flyway が投入します。

```bash
./mvnw spring-boot:run
```

## ステップ5 について

先に `git add specs && git commit && git push` で spec・plan・tasks を push します（cloud agent はリモートのリポジトリしか読めません）。
Issue の担当者に Copilot を指定すると、cloud agent が実装して PR まで作ります。
**有料の Copilot プランが必要で、Business / Enterprise では管理者がポリシーを有効にしている必要があります。**
動かない場合は講師の画面で例を見てください。結果は待たずに次へ進みます。

## 決めてあること

規約は `.specify/memory/constitution.md` にあります。主なものはこれだけです。

- REST・複数形エンドポイント・DTO と Entity の分離
- 顧客の `email` は一意、削除は論理削除、更新は PUT で全項目
- スキーマの変更は Flyway のマイグレーションを**新規に追加**する（既存は書き換えない）
- 受け入れ基準はテストとして書く。**テストが真**（spec.md は改版して持ち続ける Living Spec。constitution.md の「テスト」を参照）
