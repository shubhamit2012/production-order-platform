package com.learning.inventoryservice.infrastructure.persistence.jpa.repository;

import com.learning.inventoryservice.domain.Inventory;
import com.learning.inventoryservice.domain.ProductId;
import com.learning.inventoryservice.infrastructure.persistence.jpa.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InventoryJPARepository extends JpaRepository<InventoryEntity, Integer> {

    Optional<InventoryEntity> findByProductId(UUID productId);

}
