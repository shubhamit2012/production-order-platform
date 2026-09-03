package com.learning.orderplatform.order.application.port.out;

import com.learning.orderplatform.order.domain.Order;

public interface OrderRepository {

    void save(Order order);
}
