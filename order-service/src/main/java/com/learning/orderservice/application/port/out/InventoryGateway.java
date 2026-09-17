package com.learning.orderservice.application.port.out;

import com.learning.orderservice.application.model.InventoryReservation;

import java.util.List;

public interface InventoryGateway {

    void reserve(List<InventoryReservation> reservations);

}
