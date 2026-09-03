package com.learning.orderplatform.order.application.port.out;

import com.learning.orderplatform.order.domain.OrderId;

public interface OrderIdGenerator {

    OrderId next();
}
