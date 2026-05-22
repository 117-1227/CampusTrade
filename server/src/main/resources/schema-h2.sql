CREATE TABLE IF NOT EXISTS `user` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50) DEFAULT '',
    phone VARCHAR(20) DEFAULT '',
    avatar VARCHAR(500) DEFAULT '',
    campus VARCHAR(100) DEFAULT '',
    role VARCHAR(20) DEFAULT 'user',
    status VARCHAR(20) DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    icon VARCHAR(50) DEFAULT '',
    parent_id BIGINT,
    sort_order INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'active'
);

INSERT INTO category (id, name, icon, parent_id, sort_order) VALUES
(1, '教材教辅', 'Reading', NULL, 1),
(2, '数码产品', 'Monitor', NULL, 2),
(3, '生活用品', 'Goods', NULL, 3),
(4, '服饰鞋包', 'ShoppingBag', NULL, 4),
(5, '运动户外', 'Basketball', NULL, 5),
(6, '其他', 'More', NULL, 6);

-- 管理员账号由 DataInitializer 启动时自动创建

CREATE TABLE IF NOT EXISTS product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    original_price DECIMAL(10,2),
    `condition` VARCHAR(20) NOT NULL,
    images TEXT NOT NULL DEFAULT '[]',
    category_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'active',
    audit_status VARCHAR(20) DEFAULT 'pending',
    audit_reason VARCHAR(500),
    view_count INT DEFAULT 0,
    fav_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS favorite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_user_product (user_id, product_id)
);

CREATE TABLE IF NOT EXISTS conversation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    buyer_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    last_message TEXT,
    last_message_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_buyer_seller_product (buyer_id, seller_id, product_id)
);

CREATE TABLE IF NOT EXISTS message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    is_read TINYINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS `order` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    buyer_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'pending',
    pending_product_id BIGINT AS (
        CASE WHEN status = 'pending' THEN product_id END
    ),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_pending (pending_product_id),
    INDEX idx_buyer (buyer_id),
    INDEX idx_seller (seller_id)
);
