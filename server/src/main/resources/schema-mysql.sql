-- CampusTrade MySQL 建表脚本
-- 执行方式: mysql -u root -p campus_trade < schema-mysql.sql

CREATE TABLE IF NOT EXISTS `user` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50) DEFAULT '',
    phone VARCHAR(20) DEFAULT '',
    avatar VARCHAR(500) DEFAULT '',
    campus VARCHAR(100) DEFAULT '',
    role VARCHAR(20) DEFAULT 'user' COMMENT 'user / admin',
    status VARCHAR(20) DEFAULT 'active' COMMENT 'active / disabled',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    icon VARCHAR(50) DEFAULT '',
    parent_id BIGINT DEFAULT NULL,
    sort_order INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'active' COMMENT 'active / disabled'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    original_price DECIMAL(10,2) DEFAULT NULL,
    `condition` VARCHAR(20) NOT NULL COMMENT 'brand_new / like_new / used / worn',
    images JSON NOT NULL,
    category_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'active' COMMENT 'active / reserved / sold / hidden',
    audit_status VARCHAR(20) DEFAULT 'pending' COMMENT 'pending / approved / rejected',
    audit_reason VARCHAR(500) DEFAULT NULL,
    view_count INT DEFAULT 0,
    fav_count INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category_id),
    INDEX idx_seller (seller_id),
    INDEX idx_status (status),
    INDEX idx_audit_status (audit_status),
    FULLTEXT idx_search (title, description)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS favorite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_product (user_id, product_id),
    INDEX idx_user (user_id),
    INDEX idx_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS conversation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    buyer_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    last_message TEXT DEFAULT NULL,
    last_message_at DATETIME DEFAULT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_buyer_seller_product (buyer_id, seller_id, product_id),
    INDEX idx_buyer (buyer_id),
    INDEX idx_seller (seller_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    is_read TINYINT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_conversation_time (conversation_id, created_at),
    INDEX idx_sender (sender_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `order` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    buyer_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    status VARCHAR(20) DEFAULT 'pending' COMMENT 'pending / completed / cancelled',
    -- 保证同一商品最多一个 pending 订单；completed/cancelled 不限制
    pending_product_id BIGINT GENERATED ALWAYS AS (
        CASE WHEN status = 'pending' THEN product_id END
    ) VIRTUAL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_pending (pending_product_id),
    INDEX idx_buyer (buyer_id),
    INDEX idx_seller (seller_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ====== 种子数据 ======

INSERT INTO category (id, name, icon, parent_id, sort_order) VALUES
(1, '教材教辅', 'Reading', NULL, 1),
(2, '数码产品', 'Monitor', NULL, 2),
(3, '生活用品', 'Goods', NULL, 3),
(4, '服饰鞋包', 'ShoppingBag', NULL, 4),
(5, '运动户外', 'Basketball', NULL, 5),
(6, '其他', 'More', NULL, 6)
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- 管理员: admin / admin123
INSERT INTO `user` (username, password, nickname, phone, campus, role, status)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',
        '系统管理员', '13800000000', 'XX大学', 'admin', 'active')
ON DUPLICATE KEY UPDATE username=VALUES(username);
