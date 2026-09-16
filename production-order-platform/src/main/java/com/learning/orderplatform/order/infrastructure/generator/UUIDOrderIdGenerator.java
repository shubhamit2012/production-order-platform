package com.learning.orderplatform.order.infrastructure.generator;

import com.learning.orderplatform.order.application.port.out.OrderIdGenerator;
import com.learning.orderplatform.order.domain.OrderId;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDOrderIdGenerator implements OrderIdGenerator {

    @Override
    public OrderId next() {
        return new OrderId(UUID.randomUUID());
    }
}

