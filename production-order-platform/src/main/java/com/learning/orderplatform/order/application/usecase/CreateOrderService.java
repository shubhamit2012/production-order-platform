package com.learning.orderplatform.order.application.usecase;

import com.learning.orderplatform.order.application.model.CreateOrderCommand;
import com.learning.orderplatform.order.application.model.InventoryReservationRequest;
import com.learning.orderplatform.order.application.port.in.CreateOrderUseCase;
import com.learning.orderplatform.order.application.port.out.InventoryGateway;
import com.learning.orderplatform.order.application.port.out.OrderIdGenerator;
import com.learning.orderplatform.order.application.port.out.OrderRepository;
import com.learning.orderplatform.order.domain.Order;
import com.learning.orderplatform.order.domain.OrderId;
import com.learning.orderplatform.order.domain.OrderItem;
import com.learning.orderplatform.order.domain.exceptions.InsufficientInventoryException;

import java.util.List;

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
    public OrderId create(CreateOrderCommand command) throws InsufficientInventoryException {
        OrderId orderId = orderIdGenerator.next();

        List<InventoryReservationRequest> reservationRequests = command.items().stream()
                .map(item -> new InventoryReservationRequest(item.productId(), item.quantity()))
                .toList();

        boolean reserve = inventoryGateway.reserve(reservationRequests);
        if (!reserve) {
            throw new InsufficientInventoryException("Inventory reservation failed");
        }

        List<OrderItem> orderItems = command.items().stream()
                .map(item -> new OrderItem(item.productId(), item.quantity()))
                .toList();

        Order order = Order.create(orderId, command.customerId(), orderItems, command.createdAt());
        orderRepository.save(order);

        return order.id();
    }

}
