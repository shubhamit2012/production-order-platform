package com.learning.orderplatform.order.application.usecase;

import com.learning.orderplatform.order.application.model.CreateOrderCommand;
import com.learning.orderplatform.order.application.model.InventoryReservation;
import com.learning.orderplatform.order.application.port.in.CreateOrderUseCase;
import com.learning.orderplatform.order.application.port.out.InventoryGateway;
import com.learning.orderplatform.order.application.port.out.OrderIdGenerator;
import com.learning.orderplatform.order.application.port.out.OrderRepository;
import com.learning.orderplatform.order.domain.Order;
import com.learning.orderplatform.order.domain.OrderId;
import com.learning.orderplatform.order.domain.OrderItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreateOrderService implements CreateOrderUseCase {

    private final OrderIdGenerator orderIdGenerator;
    private final InventoryGateway inventoryGateway;
    private final OrderRepository orderRepository;

    public CreateOrderService(OrderIdGenerator orderIdGenerator, InventoryGateway inventoryGateway, OrderRepository orderRepository) {
        this.orderIdGenerator = orderIdGenerator;
        this.inventoryGateway = inventoryGateway;
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderId create(CreateOrderCommand command) {
        OrderId orderId = orderIdGenerator.next();

        List<InventoryReservation> reservations = command.items().stream()
                .map(item -> new InventoryReservation(item.productId(), item.quantity()))
                .toList();

        inventoryGateway.reserve(reservations);

        List<OrderItem> orderItems = command.items().stream()
                .map(item -> new OrderItem(item.productId(), item.quantity()))
                .toList();

        Order order = Order.create(orderId, command.customerId(), orderItems, command.createdAt());
        orderRepository.save(order);

        return order.id();
    }

}
