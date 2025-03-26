INSERT INTO tb_user (id, name, email, password)
VALUES (NEXTVAL('USER_SEQ'), 'natixis', 'natixis@natixis.com', '$2a$10$YI5zCQeLMAr6J2teF5Zxzu75IMTYCjdnLZ69uDXeH77C20gamXP6u');

INSERT INTO tb_product (id, name, description, price, user_id, enabled)
VALUES
    (NEXTVAL('PRODUCT_SEQ'), 'Laptop', 'High-performance gaming laptop', 1299.99, 1, true),
    (NEXTVAL('PRODUCT_SEQ'), 'Smartphone', 'Latest 5G smartphone with OLED display', 899.99, 1, true),
    (NEXTVAL('PRODUCT_SEQ'), 'Headphones', 'Noise-canceling over-ear headphones', 199.99, 1, true),
    (NEXTVAL('PRODUCT_SEQ'), 'Smartwatch', 'Water-resistant smartwatch with heart rate monitor', 299.99, 1, true),
    (NEXTVAL('PRODUCT_SEQ'), 'Mechanical Keyboard', 'RGB backlit mechanical keyboard', 129.99, 1, true);