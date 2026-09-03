package com.learning.orderplatform.order.application.usecase;

import com.learning.orderplatform.order.application.model.CreateOrderCommand;
import com.learning.orderplatform.order.application.port.in.CreateOrderUseCase;
import com.learning.orderplatform.order.application.port.out.InventoryGateway;
import com.learning.orderplatform.order.application.port.out.OrderIdGenerator;
import com.learning.orderplatform.order.application.port.out.OrderRepository;
import com.learning.orderplatform.order.domain.OrderId;
import org.springframework.beans.factory.annotation.Autowired;

public class CreateOrderService implements CreateOrderUseCase {

    private final OrderIdGenerator orderIdGenerator;
    private final InventoryGateway inventoryGateway;
    private final OrderRepository orderRepository;

    @Autowired
    public CreateOrderService(OrderIdGenerator orderIdGenerator, InventoryGateway inventoryGateway, OrderRepository orderRepository) {
        this.orderIdGenerator = orderIdGenerator;
        this.inventoryGateway = inventoryGateway;
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderId create(CreateOrderCommand command) {
        return null;
    }

}
