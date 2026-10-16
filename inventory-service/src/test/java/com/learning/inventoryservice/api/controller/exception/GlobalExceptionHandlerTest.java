package com.learning.inventoryservice.api.controller.exception;

import com.learning.inventoryservice.api.controller.InventoryController;
import com.learning.inventoryservice.api.mapper.ReserveInventoryMapper;
import com.learning.inventoryservice.application.exception.InsufficientInventoryException;
import com.learning.inventoryservice.application.exception.InventoryNotFoundException;
import com.learning.inventoryservice.application.exception.InvalidInventoryReservationException;
import com.learning.inventoryservice.application.usecase.ReserveInventoryUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class GlobalExceptionHandlerTest {

    private ReserveInventoryUseCase useCase;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        useCase = mock(ReserveInventoryUseCase.class);
        mvc = standaloneSetup(new InventoryController(useCase, mock(ReserveInventoryMapper.class)))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void mapsInsufficientInventoryToConflict() throws Exception {
        doThrow(new InsufficientInventoryException("Only 2 units available")).when(useCase).reserve(any());

        reserveInventory().andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("INSUFFICIENT_INVENTORY"))
                .andExpect(jsonPath("$.message").value("Only 2 units available"));
    }

    @Test
    void mapsInvalidInventoryToBadRequest() throws Exception {
        doThrow(new InvalidInventoryReservationException("Quantity must be positive")).when(useCase).reserve(any());

        reserveInventory().andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INVENTORY"))
                .andExpect(jsonPath("$.message").value("Quantity must be positive"));
    }

        @Test
        void mapsMissingInventoryToNotFound() throws Exception {
        doThrow(new InventoryNotFoundException("Inventory not found for product: missing-product"))
            .when(useCase).reserve(any());

        reserveInventory().andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value("INVENTORY_NOT_FOUND"))
            .andExpect(jsonPath("$.message").value("Inventory not found for product: missing-product"));
        }

    @Test
    void hidesUnexpectedFailureDetails() throws Exception {
        doThrow(new IllegalStateException("private database credentials")).when(useCase).reserve(any());

        reserveInventory().andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred."));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "{broken", "{}"})
    void mapsUnreadableRequestToBadRequest(String body) throws Exception {
        mvc.perform(post("/inventory/reservations").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.message").value("The request body is missing or malformed."));
    }

    private ResultActions reserveInventory() throws Exception {
        return mvc.perform(post("/inventory/reservations").contentType(MediaType.APPLICATION_JSON).content("[]"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}