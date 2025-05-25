CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name NVARCHAR(100) NOT NULL UNIQUE,
    description LONGTEXT,
    status BIT DEFAULT b'1',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    parent_id BIGINT NULL,
    is_hot BIT DEFAULT b'0',
    is_new BIT DEFAULT b'0',
    type NVARCHAR(50),
    created_by BIGINT NOT NULL,
    updated_by BIGINT NULL,
    CONSTRAINT fk_parent_category FOREIGN KEY (parent_id) REFERENCES categories(id)
    -- CONSTRAINT fk_category_created_by FOREIGN KEY (created_by) REFERENCES users(id),
    -- CONSTRAINT fk_category_updated_by FOREIGN KEY (updated_by) REFERENCES users(id)
);
