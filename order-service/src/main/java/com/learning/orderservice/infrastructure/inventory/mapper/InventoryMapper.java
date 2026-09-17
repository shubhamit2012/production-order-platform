package com.learning.orderservice.infrastructure.inventory.mapper;

import com.learning.orderservice.application.model.InventoryReservation;
import com.learning.orderservice.infrastructure.inventory.model.InventoryRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InventoryMapper {

    public List<InventoryRequest> toRequest(List<InventoryReservation> request) {
        return request.stream()
                .map(item -> new InventoryRequest(item.productId().value(), item.quantity()))
                .toList();
    }

}
