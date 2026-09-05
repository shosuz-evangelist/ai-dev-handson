-- 検索の演習で使うサンプル。category と stock に幅を持たせてある
INSERT INTO products (name, price, image_url, category, stock) VALUES
('ボールペン',       120, 'images/pen.jpg',          'writing',  120),
('油性ボールペン',   180, 'images/pen.jpg',          'writing',    0),
('ノート',           200, 'images/notebook.jpg',     'paper',     80),
('付箋',             150, 'images/sticky_notes.jpg', 'paper',     45),
('消しゴム',         100, 'images/eraser.jpg',       'writing',  200),
('定規',             180, 'images/ruler.jpg',        'tool',      60),
('ホッチキス',       500, 'images/stapler.jpg',      'tool',      12),
('クリップ',          80, 'images/clip.jpg',         'tool',       0),
('セロハンテープ',   180, 'images/tape.jpg',         'tool',      30),
('ハサミ',           350, 'images/scissors.jpg',     'tool',       8);
