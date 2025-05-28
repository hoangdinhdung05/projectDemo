-- ALTER TABLE orders ADD discount_id BIGINT;
ALTER TABLE orders ADD CONSTRAINT fk_discount FOREIGN KEY (discount_id) REFERENCES discounts(id);
