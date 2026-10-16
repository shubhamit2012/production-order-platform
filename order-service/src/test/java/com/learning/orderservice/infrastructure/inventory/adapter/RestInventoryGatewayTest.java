package com.learning.orderservice.infrastructure.inventory.adapter;

import com.learning.orderservice.application.exception.InsufficientInventoryException;
import com.learning.orderservice.application.exception.InventoryNotFoundException;
import com.learning.orderservice.application.exception.InvalidInventoryReservationException;
import com.learning.orderservice.application.exception.InventoryServiceException;
import com.learning.orderservice.infrastructure.inventory.mapper.InventoryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

class RestInventoryGatewayTest {

    private MockRestServiceServer server;
    private RestInventoryGateway gateway;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://inventory");
        server = MockRestServiceServer.bindTo(builder).build();
        gateway = new RestInventoryGateway(new InventoryMapper(), builder.build(), new InventoryRetryPolicy() {
            @Override
            public <T> T execute(Supplier<T> operation) {
                return operation.get();
            }
        });
    }

    @Test
    void preservesInsufficientInventoryMessageAndCause() {
        respond(HttpStatus.CONFLICT,
                "{\"code\":\"INSUFFICIENT_INVENTORY\",\"message\":\"Only 2 units available\"}");

        InsufficientInventoryException exception = assertThrows(InsufficientInventoryException.class,
                () -> gateway.reserve(List.of()));

        assertEquals("Only 2 units available", exception.getMessage());
        assertInstanceOf(org.springframework.web.client.RestClientResponseException.class, exception.getCause());
        server.verify();
    }

    @Test
    void preservesInvalidReservationMessage() {
        respond(HttpStatus.BAD_REQUEST,
                "{\"code\":\"INVALID_INVENTORY\",\"message\":\"Quantity must be positive\"}");

        InvalidInventoryReservationException exception = assertThrows(InvalidInventoryReservationException.class,
                () -> gateway.reserve(List.of()));

        assertEquals("Quantity must be positive", exception.getMessage());
        server.verify();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "<html>proxy error</html>", "{}", "null",
            "{\"code\":\"INSUFFICIENT_INVENTORY\",\"message\":\" \"}",
            "{\"code\":\"INTERNAL_SERVER_ERROR\",\"message\":\"private details\"}"})
    void fallsBackForUnusableConflictBody(String body) {
        respond(HttpStatus.CONFLICT, body);

        InsufficientInventoryException exception = assertThrows(InsufficientInventoryException.class,
                () -> gateway.reserve(List.of()));

        assertEquals("Insufficient inventory for the requested reservation", exception.getMessage());
        server.verify();
    }

    @Test
    void fallsBackForUnusableBadRequestBody() {
        respond(HttpStatus.BAD_REQUEST, "not JSON");

        InvalidInventoryReservationException exception = assertThrows(InvalidInventoryReservationException.class,
                () -> gateway.reserve(List.of()));

        assertEquals("Invalid inventory reservation request", exception.getMessage());
        server.verify();
    }

    @ParameterizedTest
    @ValueSource(ints = {401, 403, 429, 500, 502, 503, 504})
    void wrapsUnexpectedDownstreamStatus(int statusCode) {
        respond(HttpStatus.valueOf(statusCode), "private downstream details");

        InventoryServiceException exception = assertThrows(InventoryServiceException.class,
                () -> gateway.reserve(List.of()));

        assertEquals("Unable to communicate with the inventory service", exception.getMessage());
        server.verify();
    }

        @Test
        void preservesMissingInventoryMessageAndCauseWithoutRetrying() {
        RestClient.Builder builder = RestClient.builder().baseUrl("http://inventory");
        server = MockRestServiceServer.bindTo(builder).build();
        gateway = new RestInventoryGateway(new InventoryMapper(), builder.build(), new InventoryRetryPolicy());
        respond(HttpStatus.NOT_FOUND,
            "{\"code\":\"INVENTORY_NOT_FOUND\",\"message\":\"Inventory not found for product: missing-product\"}");

        InventoryNotFoundException exception = assertThrows(InventoryNotFoundException.class,
            () -> gateway.reserve(List.of()));

        assertEquals("Inventory not found for product: missing-product", exception.getMessage());
        assertInstanceOf(org.springframework.web.client.RestClientResponseException.class, exception.getCause());
        server.verify();
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "<html>Not Found</html>", "{}", "null",
            "{\"code\":\"INVENTORY_NOT_FOUND\",\"message\":\" \"}",
            "{\"code\":\"INTERNAL_SERVER_ERROR\",\"message\":\"private details\"}"})
        void fallsBackForUnusableNotFoundBody(String body) {
        respond(HttpStatus.NOT_FOUND, body);

        InventoryNotFoundException exception = assertThrows(InventoryNotFoundException.class,
            () -> gateway.reserve(List.of()));

        assertEquals("Inventory not found for the requested product", exception.getMessage());
        server.verify();
        }

        @Test
        void wrapsTransportFailure() {
        server.expect(requestTo("http://inventory/inventory/reservations"))
                .andRespond(request -> { throw new SocketTimeoutException("private host"); });

        InventoryServiceException exception = assertThrows(InventoryServiceException.class,
                () -> gateway.reserve(List.of()));

        assertInstanceOf(ResourceAccessException.class, exception.getCause());
        server.verify();
    }

    @Test
    void acceptsSuccessfulReservation() {
        respond(HttpStatus.CREATED, "Inventory reserved successfully");

        gateway.reserve(List.of());

        server.verify();
    }

    private void respond(HttpStatus statusCode, String body) {
        server.expect(requestTo("http://inventory/inventory/reservations"))
                .andRespond(withStatus(statusCode).contentType(MediaType.APPLICATION_JSON).body(body));
    }
}