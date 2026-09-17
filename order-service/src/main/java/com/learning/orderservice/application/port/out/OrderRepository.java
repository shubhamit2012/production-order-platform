package com.learning.orderservice.application.port.out;

import com.learning.orderservice.domain.Order;

public interface OrderRepository {

    void save(Order order);
}
