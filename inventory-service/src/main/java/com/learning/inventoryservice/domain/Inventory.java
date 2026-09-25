package com.learning.inventoryservice.domain;

import com.learning.inventoryservice.application.exception.InsufficientInventoryException;

public class Inventory {

    private final InventoryId id;
    private final ProductId productId;
    private final int reservedQuantity;
    private final int availableQuantity;

    public Inventory(InventoryId id, ProductId productId, int reservedQuantity, int availableQuantity) {
        this.id = id;
        this.productId = productId;
        this.reservedQuantity = reservedQuantity;
        this.availableQuantity = availableQuantity;
    }

    public InventoryId id() {
        return id;
    }

    public ProductId productId() {
        return productId;
    }

    public int reservedQuantity() {
        return reservedQuantity;
    }

    public int availableQuantity() {
        return availableQuantity;
    }

    public Inventory reserve(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to reserve must be greater than zero");
        }

        if (quantity > availableQuantity) {
            throw new InsufficientInventoryException("Not enough available quantity to reserve");
        }

        return new Inventory(id, productId, reservedQuantity + quantity, availableQuantity - quantity);
    }

    public Inventory release(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to release must be greater than zero");
        }

        if (quantity > reservedQuantity) {
            throw new InsufficientInventoryException("Not enough reserved quantity to release");
        }

        return new Inventory(id, productId, reservedQuantity - quantity, availableQuantity + quantity);
    }

    public Inventory restock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to restock must be greater than zero");
        }

        return new Inventory(id, productId, reservedQuantity, availableQuantity + quantity);
    }

}
