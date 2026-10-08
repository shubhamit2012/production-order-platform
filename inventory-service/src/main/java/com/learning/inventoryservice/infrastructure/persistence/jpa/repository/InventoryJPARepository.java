package com.learning.inventoryservice.infrastructure.persistence.jpa.repository;

import com.learning.inventoryservice.infrastructure.persistence.jpa.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryJPARepository extends JpaRepository<InventoryEntity, Integer> {
}
