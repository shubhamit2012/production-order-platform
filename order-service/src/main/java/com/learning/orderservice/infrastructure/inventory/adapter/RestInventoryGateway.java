package com.learning.orderservice.infrastructure.inventory.adapter;

import com.learning.orderservice.application.exception.InsufficientInventoryException;
import com.learning.orderservice.application.exception.InventoryNotFoundException;
import com.learning.orderservice.application.exception.InvalidInventoryReservationException;
import com.learning.orderservice.application.exception.InventoryServiceException;
import com.learning.orderservice.application.model.InventoryReservation;
import com.learning.orderservice.application.port.out.InventoryGateway;
import com.learning.orderservice.infrastructure.inventory.mapper.InventoryMapper;
import com.learning.orderservice.infrastructure.inventory.model.InventoryErrorResponse;
import com.learning.orderservice.infrastructure.inventory.model.InventoryRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class RestInventoryGateway implements InventoryGateway {

    private static final Logger logger = LoggerFactory.getLogger(RestInventoryGateway.class);

    private final InventoryMapper inventoryMapper;
    private final RestClient inventoryRestClient;
    private final InventoryRetryPolicy inventoryRetryPolicy;

    public RestInventoryGateway(InventoryMapper inventoryMapper, RestClient inventoryRestClient,
            InventoryRetryPolicy inventoryRetryPolicy) {
        this.inventoryMapper = inventoryMapper;
        this.inventoryRestClient = inventoryRestClient;
        this.inventoryRetryPolicy = inventoryRetryPolicy;
    }

    @Override
    public void reserve(List<InventoryReservation> reservations) throws InsufficientInventoryException {
        List<InventoryRequest> reservationRequests = inventoryMapper.toRequest(reservations);
        sendReservationRequest(reservationRequests);
    }

    private void sendReservationRequest(List<InventoryRequest> request) {
        long startedAt = System.nanoTime();
        logger.debug("Calling inventory reservation endpoint: itemCount={}", request.size());
        try {
            inventoryRetryPolicy.execute(() ->
                inventoryRestClient.post()
                        .uri("/inventory/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(request)
                        .retrieve()
                        .toBodilessEntity()
                    );
                    logger.debug("Inventory reservation call completed: itemCount={}, elapsedMs={}",
                        request.size(), TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt));
        } catch (RestClientResponseException ex) {
                    logger.debug("Inventory reservation call rejected: status={}, itemCount={}, elapsedMs={}",
                        ex.getStatusCode().value(), request.size(),
                        TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt));
            if (ex.getStatusCode().value() == 409) {
                throw new InsufficientInventoryException(businessMessage(ex, "INSUFFICIENT_INVENTORY",
                        "Insufficient inventory for the requested reservation"), ex);
            }
            if (ex.getStatusCode().value() == 400) {
                throw new InvalidInventoryReservationException(businessMessage(ex, "INVALID_INVENTORY",
                        "Invalid inventory reservation request"), ex);
            }
            if (ex.getStatusCode().value() == 404) {
                throw new InventoryNotFoundException(businessMessage(ex, "INVENTORY_NOT_FOUND",
                        "Inventory not found for the requested product"), ex);
            }
            throw new InventoryServiceException("Unable to communicate with the inventory service", ex);
        } catch (RestClientException ex) {
            logger.debug("Inventory reservation call failed: failureType={}, itemCount={}, elapsedMs={}",
                    ex.getClass().getSimpleName(), request.size(),
                    TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt));
            throw new InventoryServiceException("Unable to communicate with the inventory service", ex);
        }
    }

    private String businessMessage(RestClientResponseException exception, String expectedCode, String fallback) {
        try {
            InventoryErrorResponse error = exception.getResponseBodyAs(InventoryErrorResponse.class);
            if (error != null && expectedCode.equals(error.code())
                    && error.message() != null && !error.message().isBlank()) {
                return error.message();
            }
        } catch (RestClientException | IllegalArgumentException decodingException) {
            logger.debug("Using fallback inventory error message: status={}, expectedCode={}, reason=unreadable_body",
                exception.getStatusCode().value(), expectedCode);
            return fallback;
        }
        logger.debug("Using fallback inventory error message: status={}, expectedCode={}, reason=unrecognized_error",
            exception.getStatusCode().value(), expectedCode);
        return fallback;
    }
}
