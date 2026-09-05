-- 初期スキーマ（ハンズオンの土台）
-- products : 演習2「商品検索」で使う。category / stock は検索条件として使う
-- customers: 演習1「顧客管理」で使う。API はまだ無い（受講者が生成する）

CREATE TABLE products (
    id        BIGSERIAL PRIMARY KEY,
    name      VARCHAR(255) NOT NULL,
    price     INTEGER      NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    category  VARCHAR(50)  NOT NULL DEFAULT 'other',
    stock     INTEGER      NOT NULL DEFAULT 0
);

CREATE INDEX idx_products_category ON products (category);
CREATE INDEX idx_products_name     ON products (name);

CREATE TABLE customers (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    phone      VARCHAR(20),
    deleted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_customers_email UNIQUE (email)
);

CREATE TABLE carts (
    cart_id    BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cart_items (
    cart_item_id BIGSERIAL PRIMARY KEY,
    cart_id      BIGINT  NOT NULL,
    product_id   BIGINT  NOT NULL,
    quantity     INTEGER NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cart    FOREIGN KEY (cart_id)    REFERENCES carts(cart_id)  ON DELETE CASCADE,
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products(id)    ON DELETE RESTRICT,
    CONSTRAINT uq_cart_product UNIQUE (cart_id, product_id)
);
