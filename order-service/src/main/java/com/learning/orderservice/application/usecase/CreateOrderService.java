package com.learning.orderservice.application.usecase;

import com.learning.orderservice.application.model.CreateOrderCommand;
import com.learning.orderservice.application.model.InventoryReservation;
import com.learning.orderservice.application.port.in.CreateOrderUseCase;
import com.learning.orderservice.application.port.out.InventoryGateway;
import com.learning.orderservice.application.port.out.OrderIdGenerator;
import com.learning.orderservice.application.port.out.OrderRepository;
import com.learning.orderservice.domain.Order;
import com.learning.orderservice.domain.OrderId;
import com.learning.orderservice.domain.OrderItem;
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
