package com.learning.orderservice.application.port.out;

import com.learning.orderservice.domain.OrderId;

public interface OrderIdGenerator {

    OrderId next();
}
