package com.learning.orderplatform.order.api;

import com.learning.orderplatform.order.application.model.CreateOrderCommand;
import com.learning.orderplatform.order.application.port.in.CreateOrderUseCase;
import com.learning.orderplatform.order.domain.OrderId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final CreateOrderMapper createOrderMapper;
    private final CreateOrderUseCase createOrderUseCase;

    public OrderController(CreateOrderMapper createOrderMapper, CreateOrderUseCase createOrderUseCase) {
        this.createOrderMapper = createOrderMapper;
        this.createOrderUseCase = createOrderUseCase;
    }

    @PostMapping()
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        CreateOrderCommand createOrderCommand = createOrderMapper.toCommand(createOrderRequest);
        OrderId orderId = createOrderUseCase.create(createOrderCommand);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateOrderResponse(orderId.value()));
    }
}
