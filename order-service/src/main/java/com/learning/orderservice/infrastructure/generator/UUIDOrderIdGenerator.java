package com.learning.orderservice.infrastructure.generator;

import com.learning.orderservice.application.port.out.OrderIdGenerator;
import com.learning.orderservice.domain.OrderId;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDOrderIdGenerator implements OrderIdGenerator {

    @Override
    public OrderId next() {
        return new OrderId(UUID.randomUUID());
    }
}

