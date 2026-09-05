# Day2 EC Database — customers table

## Purpose
Day2で追加する Customer/Sales のうち、customers テーブル定義を固定します。

## Table: customers
- **id**: BIGSERIAL PRIMARY KEY
- **name**: VARCHAR(255) NOT NULL
- **email**: VARCHAR(255) NOT NULL UNIQUE
- **created_at**: TIMESTAMP NOT NULL DEFAULT now()
- **updated_at**: TIMESTAMP NOT NULL DEFAULT now()

## Notes
- テーブル名/カラム名: 小文字 + アンダースコア
- `email` は重複登録を防ぐ（研修の詰まりを減らすため、UNIQUEを固定）
