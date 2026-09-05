INSERT INTO customer (name, email, password, phone) VALUES
('田中太郎', 'tanaka@example.com', '123', '090-1234-5678'),
('佐藤花子', 'sato@example.com', '123', '080-2345-6789'),
('鈴木一郎', 'suzuki@example.com', '123', '070-3456-7890'),
('高橋美咲', 'takahashi@example.com', '123', '090-4567-8901'),
('山田次郎', 'yamada@example.com', '123', '080-5678-9012');

-- 20件の商品データを挿入
INSERT INTO products (name, price, image_url) VALUES
('ボールペン', 120, 'https://raw.githubusercontent.com/shosuz-evangelist/ai-driven-dev-day2/main/images/pen.jpg'),
('ノート', 200, 'https://raw.githubusercontent.com/shosuz-evangelist/ai-driven-dev-day2/main/images/notebook.jpg'),
('消しゴム', 100, 'https://raw.githubusercontent.com/shosuz-evangelist/ai-driven-dev-day2/main/images/eraser.jpg'),
('定規', 180, 'https://raw.githubusercontent.com/shosuz-evangelist/ai-driven-dev-day2/main/images/ruler.jpg'),
('ホッチキス', 500, 'https://raw.githubusercontent.com/shosuz-evangelist/ai-driven-dev-day2/main/images/stapler.jpg'),
('クリップ', 80, 'https://raw.githubusercontent.com/shosuz-evangelist/ai-driven-dev-day2/main/images/clip.jpg'),
('付箋', 150, 'https://raw.githubusercontent.com/shosuz-evangelist/ai-driven-dev-day2/main/images/sticky_notes.jpg'),
('セロハンテープ', 180, 'https://raw.githubusercontent.com/shosuz-evangelist/ai-driven-dev-day2/main/images/tape.jpg'),
('ハサミ', 350, 'https://raw.githubusercontent.com/shosuz-evangelist/ai-driven-dev-day2/main/images/scissors.jpg'),
('シルバーペン', 220, 'https://raw.githubusercontent.com/shosuz-evangelist/ai-driven-dev-day2/main/images/ballpoint_pen_silver.jpg'),
('テープディスペンサー', 400, 'https://raw.githubusercontent.com/shosuz-evangelist/ai-driven-dev-day2/main/images/tape_dispenser_2.jpg'),
('クリアファイル', 120, 'https://raw.githubusercontent.com/shosuz-evangelist/ai-driven-dev-day2/main/images/file.jpg'),
('バインダークリップ', 90, 'https://raw.githubusercontent.com/shosuz-evangelist/ai-driven-dev-day2/main/images/binder.jpg'),
('蛍光ペンセット', 300, 'https://raw.githubusercontent.com/shosuz-evangelist/ai-driven-dev-day2/main/images/highlighter_set.jpg'),
('テープディスペンサー（小）', 350, 'https://raw.githubusercontent.com/shosuz-evangelist/ai-driven-dev-day2/main/images/tape_dispenser_3.jpg'),
('手帳', 600, 'https://raw.githubusercontent.com/shosuz-evangelist/ai-driven-dev-day2/main/images/planner_with_glasses.jpg'),
('色鉛筆セット', 450, 'https://raw.githubusercontent.com/shosuz-evangelist/ai-driven-dev-day2/main/images/colored_pencil_set.jpg'),
('マーカー', 150, 'https://raw.githubusercontent.com/shosuz-evangelist/ai-driven-dev-day2/main/images/marker.jpg'),
('付箋セット', 250, 'https://raw.githubusercontent.com/shosuz-evangelist/ai-driven-dev-day2/main/images/sticky_notes_with_notebook.jpg'),
('スパイラルノート', 220, 'https://raw.githubusercontent.com/shosuz-evangelist/ai-driven-dev-day2/main/images/spiral_notebook_2.jpg');

INSERT INTO sale (customer_id, product_id, quantity, total_amount, sale_date) VALUES
(1, 1, 2, 240.00, '2026-01-15 10:30:00'),
(1, 5, 1, 500.00, '2026-01-15 10:35:00'),
(2, 3, 3, 300.00, '2026-01-18 14:20:00'),
(2, 7, 2, 300.00, '2026-01-18 14:25:00'),
(3, 10, 1, 220.00, '2026-01-20 09:15:00'),
(3, 12, 4, 480.00, '2026-01-20 09:20:00'),
(4, 2, 2, 400.00, '2026-01-22 16:45:00'),
(4, 6, 5, 400.00, '2026-01-22 16:50:00'),
(5, 9, 1, 350.00, '2026-01-24 11:10:00'),
(5, 14, 2, 600.00, '2026-01-24 11:15:00');
