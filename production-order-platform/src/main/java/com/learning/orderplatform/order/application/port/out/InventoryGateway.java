package com.learning.orderplatform.order.application.port.out;

import com.learning.orderplatform.order.application.model.InventoryReservationRequest;

import java.util.List;

public interface InventoryGateway {

    boolean reserve(List<InventoryReservationRequest> items);

}
