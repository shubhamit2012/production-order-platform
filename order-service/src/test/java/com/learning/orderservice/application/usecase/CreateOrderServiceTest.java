package com.learning.orderservice.application.usecase;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.learning.orderservice.application.model.CreateOrderCommand;
import com.learning.orderservice.application.model.CreateOrderItem;
import com.learning.orderservice.application.port.out.InventoryGateway;
import com.learning.orderservice.application.port.out.OrderIdGenerator;
import com.learning.orderservice.application.port.out.OrderRepository;
import com.learning.orderservice.domain.CustomerId;
import com.learning.orderservice.domain.OrderId;
import com.learning.orderservice.domain.ProductId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreateOrderServiceTest {

    private final Logger logger = (Logger) LoggerFactory.getLogger(CreateOrderService.class);
    private final ListAppender<ILoggingEvent> appender = new ListAppender<>();
    private final OrderRepository repository = mock(OrderRepository.class);
    private final OrderId orderId = new OrderId(UUID.randomUUID());
    private CreateOrderService service;

    @BeforeEach
    void setUp() {
        appender.start();
        logger.addAppender(appender);
        OrderIdGenerator generator = mock(OrderIdGenerator.class);
        when(generator.next()).thenReturn(orderId);
        service = new CreateOrderService(generator, mock(InventoryGateway.class), repository);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(appender);
        appender.stop();
    }

    @Test
    void logsOrderIdAndItemCountOnSuccess() {
        service.create(command());

        assertThat(appender.list).extracting(ILoggingEvent::getFormattedMessage)
                .contains("Creating order: orderId=" + orderId.value() + ", itemCount=1",
                        "Order persisted: orderId=" + orderId.value() + ", itemCount=1");
    }

    @Test
    void doesNotLogPersistenceSuccessWhenSavingFails() {
        doThrow(new IllegalStateException("database unavailable")).when(repository).save(any());

        assertThrows(IllegalStateException.class, () -> service.create(command()));

        assertThat(appender.list).extracting(ILoggingEvent::getFormattedMessage)
                .noneMatch(message -> message.startsWith("Order persisted:"));
    }

    private CreateOrderCommand command() {
        return new CreateOrderCommand(new CustomerId(UUID.randomUUID()),
                List.of(new CreateOrderItem(new ProductId(UUID.randomUUID()), 2)), Instant.now());
    }
}