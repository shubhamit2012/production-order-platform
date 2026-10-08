package com.learning.inventoryservice.application.usecase;

import com.learning.inventoryservice.application.command.ReserveInventoryCommand;
import com.learning.inventoryservice.application.port.out.InventoryRepository;
import com.learning.inventoryservice.domain.Inventory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReserveInventoryService implements ReserveInventoryUseCase {

    private final InventoryRepository inventoryRepository;

    public ReserveInventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @Transactional
    public void reserve(ReserveInventoryCommand command) {
        command.inventoryItems().forEach(inventoryItem -> {
            Inventory inventory = inventoryRepository.findByProductId(inventoryItem.productId());
            Inventory updatedInventory = inventory.reserve(inventoryItem.quantity());
            inventoryRepository.save(updatedInventory);
        });
    }
}
