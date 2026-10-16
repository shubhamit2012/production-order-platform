package com.learning.orderservice.api.exception;

import com.learning.orderservice.api.controller.OrderController;
import com.learning.orderservice.api.mapper.CreateOrderMapper;
import com.learning.orderservice.application.exception.InsufficientInventoryException;
import com.learning.orderservice.application.exception.InventoryNotFoundException;
import com.learning.orderservice.application.exception.InvalidInventoryReservationException;
import com.learning.orderservice.application.exception.InventoryServiceException;
import com.learning.orderservice.application.port.in.CreateOrderUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class GlobalExceptionHandlerTest {

    private CreateOrderUseCase useCase;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        useCase = mock(CreateOrderUseCase.class);
        mvc = standaloneSetup(new OrderController(mock(CreateOrderMapper.class), useCase))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void mapsInsufficientInventoryToConflict() throws Exception {
        when(useCase.create(any())).thenThrow(new InsufficientInventoryException("Only 2 units available", null));

        createOrder().andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INSUFFICIENT_INVENTORY"))
                .andExpect(jsonPath("$.message").value("Only 2 units available"));
    }

    @Test
    void mapsInvalidInventoryToBadRequest() throws Exception {
        when(useCase.create(any())).thenThrow(new InvalidInventoryReservationException("Quantity must be positive", null));

        createOrder().andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INVENTORY"))
                .andExpect(jsonPath("$.message").value("Quantity must be positive"));
    }

        @Test
        void mapsMissingInventoryToNotFound() throws Exception {
        when(useCase.create(any())).thenThrow(
            new InventoryNotFoundException("Inventory not found for product: missing-product", null));

        createOrder().andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("INVENTORY_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("Inventory not found for product: missing-product"));
        }

    @Test
    void hidesDownstreamFailureDetails() throws Exception {
        when(useCase.create(any())).thenThrow(new InventoryServiceException("private downstream host", null));

        createOrder().andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("INVENTORY_SERVICE_ERROR"))
                .andExpect(jsonPath("$.message").value(
                        "The order could not be processed because the inventory service is temporarily unavailable."));
    }

    @Test
    void hidesUnexpectedFailureDetails() throws Exception {
        when(useCase.create(any())).thenThrow(new IllegalStateException("private database credentials"));

        createOrder().andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "{broken", "[]"})
    void mapsUnreadableRequestToBadRequest(String body) throws Exception {
        mvc.perform(post("/order").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value("The request body is missing or malformed."));
    }

    private ResultActions createOrder() throws Exception {
        return mvc.perform(post("/order").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}