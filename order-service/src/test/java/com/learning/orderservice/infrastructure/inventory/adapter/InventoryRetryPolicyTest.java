package com.learning.orderservice.infrastructure.inventory.adapter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InventoryRetryPolicyTest {

    private final Logger logger = (Logger) LoggerFactory.getLogger(InventoryRetryPolicy.class);
    private final ListAppender<ILoggingEvent> appender = new ListAppender<>();
    private final InventoryRetryPolicy policy = new InventoryRetryPolicy();

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
    void logsRetryAttemptAndBackoffWithoutExceptionDetails() {
        AtomicInteger attempts = new AtomicInteger();

        String result = policy.execute(() -> {
            if (attempts.incrementAndGet() == 1) {
                throw new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE, "private downstream details");
            }
            return "reserved";
        });

        assertThat(result).isEqualTo("reserved");
        assertThat(attempts.get()).isEqualTo(2);
        assertThat(appender.list).filteredOn(event -> event.getLevel() == Level.WARN)
                .extracting(ILoggingEvent::getFormattedMessage)
                .containsExactly("Retrying inventory operation: nextAttempt=2, maxAttempts=3, backoffMs=2000, "
                        + "failureType=HttpServerErrorException");
    }

    @ParameterizedTest
    @ValueSource(ints = {400, 404, 409})
    void doesNotLogRetryForBusinessRejection(int statusCode) {
        AtomicInteger attempts = new AtomicInteger();

        assertThrows(HttpClientErrorException.class, () -> policy.execute(() -> {
            attempts.incrementAndGet();
            throw new HttpClientErrorException(HttpStatus.valueOf(statusCode));
        }));

        assertThat(attempts.get()).isEqualTo(1);
        assertThat(appender.list).noneMatch(event -> event.getLevel() == Level.WARN);
    }

    @Test
    void doesNotLogRetryForTimeout() {
        AtomicInteger attempts = new AtomicInteger();

        assertThrows(ResourceAccessException.class, () -> policy.execute(() -> {
            attempts.incrementAndGet();
            throw new ResourceAccessException("private downstream host", new SocketTimeoutException());
        }));

        assertThat(attempts.get()).isEqualTo(1);
        assertThat(appender.list).noneMatch(event -> event.getLevel() == Level.WARN);
    }

    @Test
    void logsInterruptionAndPreservesInterruptFlag() {
        Thread.currentThread().interrupt();
        try {
            assertThrows(IllegalStateException.class, () -> policy.execute(() -> {
                throw new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE);
            }));

            assertThat(Thread.currentThread().isInterrupted()).isTrue();
            assertThat(appender.list).extracting(ILoggingEvent::getFormattedMessage)
                    .contains("Inventory retry interrupted: attempt=1");
        } finally {
            Thread.interrupted();
        }
    }
}