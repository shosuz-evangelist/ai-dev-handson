# Phase 0: 環境確認・セットアップ

## 目的

開発環境（Java, PostgreSQL, VS Code）の動作確認とGitHub Copilot Enterpriseの動作確認を行う。

## 成果物

- 環境チェックリスト完了
- VS Code拡張機能の動作確認
- GitHub Copilot Enterpriseのライセンス・応答確認
- Spec Kitフォルダ（.github/spec/）の存在確認

## 前提条件

- Java 21がインストール済み
- PostgreSQL 15がインストール済み
- VS Codeがインストール済み
- GitHub Copilot Enterprise拡張機能がインストール済み

## 実装手順

### 1. Java バージョン確認

```bash
java -version
```bash

**期待される出力:**

```bash
openjdk version "21.x.x"
```bash

### 2. PostgreSQL 接続確認

```bash
psql -U postgres -h localhost -p 5432
```bash

**pgAdmin を使用する場合:**

- Host: localhost
- Port: 5432
- Username: postgres
- Database: postgres

### 3. VS Code 拡張機能確認

必須拡張機能:

- GitHub Copilot
- GitHub Copilot Chat
- Java Extension Pack
- Spring Boot Extension Pack
- PostgreSQL

### 4. プロジェクトを開く

```bash
cd ai-driven-dev-training-ec-site
code .
```bash

### 5. .github/spec/ フォルダの確認

プロジェクトルートに `.github/spec/` フォルダが存在することを確認:

```bash
ls -la .github/spec/
```bash

### 6. GitHub Copilot の動作確認

VS Code で GitHub Copilot Chat を開き、以下のプロンプトを実行:

```bash
@workspace このプロジェクトで使用している技術スタックを教えて
```bash

```bash
@workspace .github/spec フォルダには何が含まれていますか？
```bash

```bash
@workspace この講義で最初にやるべき手順を箇条書きで教えて
```bash

### 7. README の確認

プロジェクトルートの `README.md` を開き、プロジェクト構成を理解する。

## よくあるエラー

### エラー1: Copilot未有効 / Proxyで応答なし

**対策:**

- GitHub Copilot Enterpriseのライセンス確認
- ネットワーク/Proxy設定の確認
- VS Codeの再起動

### エラー2: PostgreSQL起動していない

**対策:**

- サービス起動確認
- ポート5432が使用可能か確認

```bash
# Windows
net start postgresql-x64-15

# macOS
brew services start postgresql@15

# Linux
sudo systemctl start postgresql
```bash

### エラー3: Java バージョン不一致

**対策:**

- Java 21をインストール
- JAVA_HOME環境変数の設定確認

## FAQ

### Q: Spec Kitとは？

**A:** `.github/spec/*.md`に仕様を置き、AIが参照するための仕様書駆動開発フレームワークです。GitHub Copilotに`@workspace /spec`コマンドで仕様書を読み込ませることで、正確なコード生成が可能になります。

### Q: 画像はどこに？

**A:** URLのみ保持（GitHub raw）し、DBのimage_urlカラムに格納します。Base64は使用しません。

### Q: @workspace コマンドとは？

**A:** GitHub Copilot Chatでプロジェクト全体のコンテキスト（ファイル構成、依存関係、仕様書など）を理解して回答するためのコマンドです。

## 所要時間

約30分

## 次のステップ

Phase 1: Hello World API の実装に進む
