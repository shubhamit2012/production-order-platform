package com.learning.orderplatform.order.infrastructure.inventory;

import com.learning.orderplatform.order.application.model.InventoryReservationRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InventoryMapper {

    public List<InventoryRequest> toRequest(List<InventoryReservationRequest> request) {
        return request.stream()
                .map(item -> new InventoryRequest(item.productId().value(), item.quantity()))
                .toList();
    }

}
