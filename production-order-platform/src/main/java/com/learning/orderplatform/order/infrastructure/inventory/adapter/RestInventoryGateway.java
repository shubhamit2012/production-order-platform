package com.learning.orderplatform.order.infrastructure.inventory.adapter;

import com.learning.orderplatform.order.application.exception.InsufficientInventoryException;
import com.learning.orderplatform.order.application.model.InventoryReservation;
import com.learning.orderplatform.order.application.port.out.InventoryGateway;
import com.learning.orderplatform.order.infrastructure.inventory.mapper.InventoryMapper;
import com.learning.orderplatform.order.infrastructure.inventory.model.InventoryRequest;
import com.learning.orderplatform.order.infrastructure.inventory.model.InventoryResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RestInventoryGateway implements InventoryGateway {

    private final InventoryMapper inventoryMapper;

    public RestInventoryGateway(InventoryMapper inventoryMapper) {
        this.inventoryMapper = inventoryMapper;
    }

    @Override
    public void reserve(List<InventoryReservation> reservations) throws InsufficientInventoryException {
        List<InventoryRequest> reservationRequests = inventoryMapper.toRequest(reservations);
        InventoryResponse inventoryResponse = sendReservationRequest(reservationRequests);
    }

    private InventoryResponse sendReservationRequest(List<InventoryRequest> request) {
        // Implement the logic to call the external inventory service and reserve the items
        // For now, we will just return true to indicate that the reservation succeed
        return new InventoryResponse(true);
    }
}
