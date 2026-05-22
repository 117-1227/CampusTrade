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

-- 测试管理员: admin / admin123
INSERT INTO `user` (username, password, nickname, phone, campus, role, status)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi',
        '系统管理员', '13800000000', 'XX大学', 'admin', 'active');
