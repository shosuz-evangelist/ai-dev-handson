# 商品画像フォルダ

このフォルダには、EC サイトで使用する商品画像（文房具 20 種類）を配置します。

## 画像ファイル一覧

以下の 20 個の画像ファイルが配置されています：

1. `pen.jpg` - ボールペン
2. `notebook.jpg` - ノート
3. `eraser.jpg` - 消しゴム
4. `ruler.jpg` - 定規
5. `stapler.jpg` - ホッチキス
6. `clip.jpg` - クリップ
7. `sticky_notes.jpg` - 付箋
8. `tape.jpg` - セロハンテープ
9. `scissors.jpg` - ハサミ
10. `ballpoint_pen_silver.jpg` - シルバーペン
11. `tape_dispenser_2.jpg` - テープディスペンサー
12. `file.jpg` - クリアファイル
13. `binder.jpg` - バインダークリップ
14. `highlighter_set.jpg` - 蛍光ペンセット
15. `tape_dispenser_3.jpg` - テープディスペンサー
16. `planner_with_glasses.jpg` - 手帳
17. `colored_pencil_set.jpg` - 色鉛筆セット
18. `marker.jpg` - マーカー
19. `sticky_notes_with_notebook.jpg` - 付箋セット
20. `spiral_notebook_2.jpg` - スパイラルノート

## 画像仕様

- **形式**: JPG
- **推奨サイズ**: 800x800px
- **ファイルサイズ**: 各 100KB 以下推奨

## GitHub での参照

これらの画像は、GitHub リポジトリ公開後、以下の URL で参照されます：

```
https://raw.githubusercontent.com/shosuz-evangelist/ai-driven-dev-training-speckit/main/images/pen.jpg
```

PostgreSQL の `products` テーブルの `image_url` カラムに、この URL が格納されます。

## Day 1 と Day 2 での利用

- **Day 1（ローカル PostgreSQL）**: GitHub の画像 URL を参照
- **Day 2（AWS RDS）**: 同じ GitHub の画像 URL を参照

→ DB 移行しても画像 URL の変更は不要です！

## 注意事項

- 著作権に注意してください
- 商用利用可能な画像のみを使用
- 本研修では学習目的のため使用しています
