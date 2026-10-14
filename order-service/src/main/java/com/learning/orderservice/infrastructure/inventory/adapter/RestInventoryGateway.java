package com.learning.orderservice.infrastructure.inventory.adapter;

import com.learning.orderservice.application.exception.InsufficientInventoryException;
import com.learning.orderservice.application.exception.InvalidInventoryReservationException;
import com.learning.orderservice.application.model.InventoryReservation;
import com.learning.orderservice.application.port.out.InventoryGateway;
import com.learning.orderservice.infrastructure.inventory.mapper.InventoryMapper;
import com.learning.orderservice.infrastructure.inventory.model.InventoryRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class RestInventoryGateway implements InventoryGateway {

    private final InventoryMapper inventoryMapper;
    private final RestClient inventoryRestClient;

    public RestInventoryGateway(InventoryMapper inventoryMapper, RestClient inventoryRestClient) {
        this.inventoryMapper = inventoryMapper;
        this.inventoryRestClient = inventoryRestClient;
    }

    @Override
    public void reserve(List<InventoryReservation> reservations) throws InsufficientInventoryException {
        List<InventoryRequest> reservationRequests = inventoryMapper.toRequest(reservations);
        sendReservationRequest(reservationRequests);
    }

    private void sendReservationRequest(List<InventoryRequest> request) {
        try {
            inventoryRestClient.post()
                    .uri("/inventory/reservations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.Conflict ex) {
            throw new InsufficientInventoryException(ex.getMessage());
        } catch (HttpClientErrorException.BadRequest ex) {
            throw new InvalidInventoryReservationException(ex.getMessage());
        }
    }
}
