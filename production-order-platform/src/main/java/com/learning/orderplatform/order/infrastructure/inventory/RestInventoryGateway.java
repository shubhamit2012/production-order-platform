package com.learning.orderplatform.order.infrastructure.inventory;

import com.learning.orderplatform.order.application.model.InventoryReservationRequest;
import com.learning.orderplatform.order.application.port.out.InventoryGateway;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RestInventoryGateway implements InventoryGateway {

    private final InventoryMapper inventoryMapper;

    public RestInventoryGateway(InventoryMapper inventoryMapper) {
        this.inventoryMapper = inventoryMapper;
    }

    @Override
    public boolean reserve(List<InventoryReservationRequest> items) {
        List<InventoryRequest> request = inventoryMapper.toRequest(items);
        InventoryResponse inventoryResponse = sendReservationRequest(request);
        return inventoryResponse.reserved();
    }

    private InventoryResponse sendReservationRequest(List<InventoryRequest> request) {
        // Implement the logic to call the external inventory service and reserve the items
        // For now, we will just return true to indicate that the reservation succeed
        return new InventoryResponse(true);
    }
}
