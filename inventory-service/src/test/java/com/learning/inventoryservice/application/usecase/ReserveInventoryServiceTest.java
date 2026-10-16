package com.learning.inventoryservice.application.usecase;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.learning.inventoryservice.application.command.InventoryItemCommand;
import com.learning.inventoryservice.application.command.ReserveInventoryCommand;
import com.learning.inventoryservice.application.exception.InventoryNotFoundException;
import com.learning.inventoryservice.application.port.out.InventoryRepository;
import com.learning.inventoryservice.domain.Inventory;
import com.learning.inventoryservice.domain.InventoryId;
import com.learning.inventoryservice.domain.ProductId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReserveInventoryServiceTest {

    private final Logger logger = (Logger) LoggerFactory.getLogger(ReserveInventoryService.class);
    private final ListAppender<ILoggingEvent> appender = new ListAppender<>();
    private final InventoryRepository repository = mock(InventoryRepository.class);
    private final ProductId productId = new ProductId(UUID.randomUUID());
    private final ReserveInventoryService service = new ReserveInventoryService(repository);

    @BeforeEach
    void setUp() {
        appender.start();
        logger.addAppender(appender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(appender);
        appender.stop();
    }

    @Test
    void logsReservationStartAndPreparedUpdates() {
        when(repository.findByProductId(productId))
                .thenReturn(new Inventory(new InventoryId(UUID.randomUUID()), productId, 0, 10));

        service.reserve(command());

        assertThat(appender.list).extracting(ILoggingEvent::getFormattedMessage)
                .contains("Reserving inventory: itemCount=1", "Inventory reservation updates prepared: itemCount=1");
    }

    @Test
    void doesNotLogPreparedUpdatesWhenReservationFails() {
        when(repository.findByProductId(productId)).thenThrow(new InventoryNotFoundException("Missing inventory"));

        assertThrows(InventoryNotFoundException.class, () -> service.reserve(command()));

        assertThat(appender.list).extracting(ILoggingEvent::getFormattedMessage)
                .noneMatch(message -> message.startsWith("Inventory reservation updates prepared:"));
    }

    private ReserveInventoryCommand command() {
        return new ReserveInventoryCommand(List.of(new InventoryItemCommand(productId, 2)));
    }
}