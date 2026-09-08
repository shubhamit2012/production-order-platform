package com.learning.orderplatform.order.api;

import com.learning.orderplatform.order.application.model.CreateOrderCommand;
import com.learning.orderplatform.order.application.port.in.CreateOrderUseCase;
import com.learning.orderplatform.order.domain.OrderId;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController("/order")
public class OrderController {

    private final CreateOrderMapper createOrderMapper;
    private final CreateOrderUseCase createOrderUseCase;

    public OrderController(CreateOrderMapper createOrderMapper, CreateOrderUseCase createOrderUseCase) {
        this.createOrderMapper = createOrderMapper;
        this.createOrderUseCase = createOrderUseCase;
    }

    @PostMapping()
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        CreateOrderCommand createOrderCommand = createOrderMapper.toCreateOrderCommand(createOrderRequest);
        OrderId orderId = createOrderUseCase.create(createOrderCommand);
        return new ResponseEntity<>(new CreateOrderResponse(orderId.value()), HttpStatusCode.valueOf(201));
    }
}
