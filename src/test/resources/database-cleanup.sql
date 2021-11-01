ALTER TABLE orders
DROP
CONSTRAINT fk_orders_order_table;

ALTER TABLE order_line_item
DROP
CONSTRAINT fk_order_line_item_orders;

ALTER TABLE order_line_item
DROP
CONSTRAINT fk_order_line_item_menu;

ALTER TABLE menu
DROP
CONSTRAINT fk_menu_menu_group;

ALTER TABLE menu_product
DROP
CONSTRAINT fk_menu_product_menu;

ALTER TABLE menu_product
DROP
CONSTRAINT fk_menu_product_product;

ALTER TABLE order_table
DROP
CONSTRAINT fk_order_table_table_group;

TRUNCATE TABLE orders;
TRUNCATE TABLE order_line_item;
TRUNCATE TABLE menu;
TRUNCATE TABLE menu_group;
TRUNCATE TABLE menu_product;
TRUNCATE TABLE order_table;
TRUNCATE TABLE table_group;
TRUNCATE TABLE product;

ALTER TABLE orders AUTO_INCREMENT = 1;
ALTER TABLE order_line_item AUTO_INCREMENT = 1;
ALTER TABLE menu AUTO_INCREMENT = 1;
ALTER TABLE menu_group AUTO_INCREMENT = 1;
ALTER TABLE menu_product AUTO_INCREMENT = 1;
ALTER TABLE order_table AUTO_INCREMENT = 1;
ALTER TABLE table_group AUTO_INCREMENT = 1;
ALTER TABLE product AUTO_INCREMENT = 1;

ALTER TABLE orders
    ADD CONSTRAINT fk_orders_order_table
        FOREIGN KEY (order_table_id) REFERENCES order_table (id);

ALTER TABLE order_line_item
    ADD CONSTRAINT fk_order_line_item_orders
        FOREIGN KEY (order_id) REFERENCES orders (id);

ALTER TABLE order_line_item
    ADD CONSTRAINT fk_order_line_item_menu
        FOREIGN KEY (menu_id) REFERENCES menu (id);

ALTER TABLE menu
    ADD CONSTRAINT fk_menu_menu_group
        FOREIGN KEY (menu_group_id) REFERENCES menu_group (id);

ALTER TABLE menu_product
    ADD CONSTRAINT fk_menu_product_menu
        FOREIGN KEY (menu_id) REFERENCES menu (id);

ALTER TABLE menu_product
    ADD CONSTRAINT fk_menu_product_product
        FOREIGN KEY (product_id) REFERENCES product (id);

ALTER TABLE order_table
    ADD CONSTRAINT fk_order_table_table_group
        FOREIGN KEY (table_group_id) REFERENCES table_group (id);
