package com.learning.inventoryservice.infrastructure.persistence.adapter;

import com.learning.inventoryservice.application.port.out.InventoryRepository;
import com.learning.inventoryservice.domain.Inventory;
import com.learning.inventoryservice.infrastructure.persistence.jpa.entity.InventoryEntity;
import com.learning.inventoryservice.infrastructure.persistence.jpa.repository.InventoryJPARepository;
import com.learning.inventoryservice.infrastructure.persistence.mapper.InventoryEntityMapper;
import org.springframework.stereotype.Repository;

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
        inventoryEntity = inventoryJPARepository.save(inventoryEntity);
    }

}
