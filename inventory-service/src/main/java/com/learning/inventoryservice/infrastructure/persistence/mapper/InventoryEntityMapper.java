package com.learning.inventoryservice.infrastructure.persistence.mapper;

import com.learning.inventoryservice.domain.Inventory;
import com.learning.inventoryservice.infrastructure.persistence.jpa.entity.InventoryEntity;
import org.springframework.stereotype.Component;

@Component
public class InventoryEntityMapper {

    public InventoryEntity toEntity(Inventory inventory) {
        return new InventoryEntity(
                inventory.id().value(),
                inventory.productId().value(),
                inventory.reservedQuantity(),
                inventory.availableQuantity()
        );
    }
}
