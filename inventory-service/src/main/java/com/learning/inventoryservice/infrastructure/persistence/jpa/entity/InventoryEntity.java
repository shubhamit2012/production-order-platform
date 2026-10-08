package com.learning.inventoryservice.infrastructure.persistence.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "inventory")
public class InventoryEntity {

    @Id
    private UUID id;

    @Column
    private UUID productId;

    @Column
    private int reservedQuantity;

    @Column
    private int availableQuantity;

    public InventoryEntity() {
    }

    public InventoryEntity(UUID id, UUID productId, int reservedQuantity, int availableQuantity) {
        this.id = id;
        this.productId = productId;
        this.reservedQuantity = reservedQuantity;
        this.availableQuantity = availableQuantity;
    }



}
