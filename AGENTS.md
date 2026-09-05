# AGENTS.md

このリポジトリで作業する AI エージェント（GitHub Copilot / Codex / Claude Code）の共通の入口です。
常時読み込まれる層なので短く保ちます。詳しい規約は参照先に置いています。

## 参照

- プロジェクトの規約（SSOT）: [.specify/memory/constitution.md](.specify/memory/constitution.md)
- Spec Kit のスキル: `.github/skills/speckit-*/SKILL.md`

## このリポジトリの構成

- `backend/` … Spring Boot の EC バックエンド（Java 21 / PostgreSQL 15 / Flyway）
- `backend/src/main/resources/db/migration/` … スキーマ。**直接編集しない**（Hooks で止まります）
- `.github/spec/reference/` … 既存の設計資料（参考）
- `specs/` … Spec Kit が生成する spec / plan / tasks（作業後に生成されます）

## よく使うコマンド

```bash
cd backend
./mvnw test          # テスト
./mvnw spring-boot:run   # 起動（PostgreSQL が必要）
```

## 守ること

- スキーマの変更は Flyway のマイグレーションを新規に追加する（既存ファイルは書き換えない）
- 受け入れ基準はテストに落とす。テストが真であり、spec.md は再生成できる中間生成物
- 依頼された範囲の外を勝手に直さない。気づいた点は最後に提案として分ける
