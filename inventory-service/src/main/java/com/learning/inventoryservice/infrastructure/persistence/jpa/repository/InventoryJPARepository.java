package com.learning.inventoryservice.infrastructure.persistence.jpa.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.learning.inventoryservice.infrastructure.persistence.jpa.entity.InventoryEntity;

public interface InventoryJPARepository extends JpaRepository<InventoryEntity, UUID> {

    Optional<InventoryEntity> findByProductId(UUID productId);

}
