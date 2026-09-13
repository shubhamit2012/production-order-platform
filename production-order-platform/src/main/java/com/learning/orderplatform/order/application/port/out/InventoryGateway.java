package com.learning.orderplatform.order.application.port.out;

import com.learning.orderplatform.order.application.model.InventoryReservation;

import java.util.List;

public interface InventoryGateway {

    void reserve(List<InventoryReservation> reservations);

}
