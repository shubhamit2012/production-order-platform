package com.learning.orderplatform.order.infrastructure.persistence.jpa.repository;

import com.learning.orderplatform.order.infrastructure.persistence.jpa.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderEntityJPARepository extends JpaRepository<OrderEntity, UUID> {
}
