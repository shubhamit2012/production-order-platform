package com.learning.inventoryservice.application.usecase;

import com.learning.inventoryservice.application.command.ReserveInventoryCommand;
import com.learning.inventoryservice.application.port.out.InventoryRepository;
import com.learning.inventoryservice.domain.Inventory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReserveInventoryService implements ReserveInventoryUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ReserveInventoryService.class);

    private final InventoryRepository inventoryRepository;

    public ReserveInventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @Transactional
    public void reserve(ReserveInventoryCommand command) {
        logger.info("Reserving inventory: itemCount={}", command.inventoryItems().size());
        command.inventoryItems().forEach(inventoryItem -> {
            logger.debug("Reserving inventory item: productId={}, quantity={}",
                    inventoryItem.productId().value(), inventoryItem.quantity());
            Inventory inventory = inventoryRepository.findByProductId(inventoryItem.productId());
            Inventory updatedInventory = inventory.reserve(inventoryItem.quantity());
            inventoryRepository.save(updatedInventory);
        });
        logger.info("Inventory reservation updates prepared: itemCount={}", command.inventoryItems().size());
    }
}
