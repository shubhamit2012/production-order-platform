CREATE TABLE orders (
    id          UUID                     NOT NULL,
    customer_id UUID                     NOT NULL,
    status      VARCHAR(255)             NOT NULL,
    created_at  TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_orders PRIMARY KEY (id)
);

-- order_id is nullable: Hibernate inserts items before linking them via the unidirectional @JoinColumn
CREATE TABLE order_items (
    id         UUID    NOT NULL,
    order_id   UUID,
    product_id UUID    NOT NULL,
    quantity   INTEGER NOT NULL,
    CONSTRAINT pk_order_items PRIMARY KEY (id),
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE
);

CREATE INDEX idx_order_items_order_id ON order_items (order_id);
