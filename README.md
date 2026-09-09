# AI 駆動開発ハンズオン（4 時間・初級）

仕様駆動で EC バックエンドを作るハンズオンの教材リポジトリです。
**環境構築はしません。** 初期状態から、そのまま本題に入れます。

## はじめかた（受講者の方へ）

1. GitHub にサインインします。
2. このページの右上の **Fork** を押して、自分のアカウントにコピーします。
   演習の後半で Issue に Copilot を割り当てるため、**自分のリポジトリである必要があります**。
3. 自分の fork の **Code** から **Codespaces** を作ります（ブラウザだけで開けます）。
   作れない場合は、HTTPS の URL を `git clone` して VS Code で開きます。
4. `cd backend && ./mvnw -B test` を実行し、`Tests run: 5` が出れば準備完了です。

## 何が入っているか

| | 中身 |
|---|---|
| `backend/` | Spring Boot の EC バックエンド（Java 21 / PostgreSQL 15 / Flyway） |
| `.specify/` | Spec Kit 本体。規約は `.specify/memory/constitution.md` |
| `.github/skills/speckit-*/` | Spec Kit のコマンド（Agent Skills 形式・10 個） |
| `.github/hooks/hooks.json` | 強制層の例。スキーマの直接編集を止め、編集後にテストを実行する |
| `AGENTS.md` | AI ツール共通の入口。規約へ 1 行で参照する |
| `.github/spec/reference/` | 既存の設計資料（参考） |

`products` と `customers` のテーブルは最初から用意してあります。
**customers の API はまだありません。** それを作るのが演習1です。

## コマンドの打ち方

Spec Kit v1.0 から、Copilot 向けの配布が **Agent Skills 形式**になりました。
コマンド名は `.github/skills/` のディレクトリ名がそのまま使われるため、**ハイフン**です。

```
/speckit-specify        （旧: /speckit.specify）
/speckit-clarify
/speckit-taskstoissues
```

必須 7 = `constitution` / `specify` / `plan` / `tasks` / `taskstoissues` / `implement` / `converge`
任意 3 = `clarify` / `analyze` / `checklist`

## 当日の流れ（8 ステップ）

演習1（顧客管理）で講師と一緒に一周し、演習2（商品検索）を自力で一周します。

1. 意図を一言から始める
2. `/speckit-specify` で spec / plan / tasks を生成
3. `/speckit-clarify` で曖昧さを潰す
4. `/speckit-taskstoissues` で Issue 化
5. Issue を Copilot に渡して PR を受け取る
6. 実装（仕様を参照して生成させる）
7. テスト = 受け入れ基準 = 真
8. Hooks で機械的に強制する

## 始め方

```bash
git clone https://github.com/shosuz-evangelist/ai-dev-handson
cd ai-dev-handson/backend
./mvnw test          # 既存テストが緑になることを確認
```

起動には PostgreSQL が要ります。`backend/src/main/resources/application.properties` の
接続先を自分の環境に合わせてください。スキーマとサンプルデータは Flyway が投入します。

```bash
./mvnw spring-boot:run
```

## ステップ5 について

Issue の担当者に Copilot を指定すると、cloud agent が実装して PR まで作ります。
**有料の Copilot プランが必要で、Business / Enterprise では管理者がポリシーを有効にしている必要があります。**
動かない場合は講師の画面で例を見てください。結果は待たずに次へ進みます。

## 決めてあること

規約は `.specify/memory/constitution.md` にあります。主なものはこれだけです。

- REST・複数形エンドポイント・DTO と Entity の分離
- 顧客の `email` は一意、削除は論理削除、更新は PUT で全項目
- スキーマの変更は Flyway のマイグレーションを**新規に追加**する（既存は書き換えない）
- 受け入れ基準はテストとして書く。**テストが真**で、`spec.md` は再生成できる中間生成物
