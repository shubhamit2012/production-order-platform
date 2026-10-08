package com.learning.inventoryservice.infrastructure.persistence.adapter;

import com.learning.inventoryservice.application.port.out.InventoryRepository;
import com.learning.inventoryservice.domain.Inventory;
import com.learning.inventoryservice.domain.ProductId;
import com.learning.inventoryservice.infrastructure.persistence.jpa.entity.InventoryEntity;
import com.learning.inventoryservice.infrastructure.persistence.jpa.repository.InventoryJPARepository;
import com.learning.inventoryservice.infrastructure.persistence.mapper.InventoryEntityMapper;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class InventoryRepositoryAdapter implements InventoryRepository {

    private final InventoryJPARepository inventoryJPARepository;
    private final InventoryEntityMapper inventoryEntityMapper;

    public InventoryRepositoryAdapter(InventoryJPARepository inventoryJPARepository, InventoryEntityMapper inventoryEntityMapper) {
        this.inventoryJPARepository = inventoryJPARepository;
        this.inventoryEntityMapper = inventoryEntityMapper;
    }

    @Override
    public void save(Inventory inventory) {
        InventoryEntity inventoryEntity = inventoryEntityMapper.toEntity(inventory);
        inventoryJPARepository.save(inventoryEntity);
    }

    @Override
    public Inventory findByProductId(ProductId productId) {
        InventoryEntity inventoryEntity = inventoryJPARepository.findByProductId(productId.value())
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found for product: " + productId.value()));
        return inventoryEntityMapper.toDomain(inventoryEntity);
    }

}
