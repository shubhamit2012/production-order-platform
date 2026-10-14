CREATE TABLE inventory (
    id                 UUID    NOT NULL,
    product_id         UUID    NOT NULL,
    reserved_quantity  INTEGER NOT NULL DEFAULT 0,
    available_quantity INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT pk_inventory PRIMARY KEY (id),
    CONSTRAINT uk_inventory_product_id UNIQUE (product_id)
);
