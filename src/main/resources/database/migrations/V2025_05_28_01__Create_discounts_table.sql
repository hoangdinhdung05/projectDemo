CREATE TABLE discounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    discount_amount DECIMAL(15, 2) NOT NULL,
    is_percentage BOOLEAN NOT NULL,
    start_date DATETIME,
    end_date DATETIME,
    max_usage INT,
    used_count INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE
);