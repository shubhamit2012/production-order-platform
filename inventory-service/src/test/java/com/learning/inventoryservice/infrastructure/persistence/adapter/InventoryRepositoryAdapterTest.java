package com.learning.inventoryservice.infrastructure.persistence.adapter;

import com.learning.inventoryservice.application.exception.InventoryNotFoundException;
import com.learning.inventoryservice.domain.ProductId;
import com.learning.inventoryservice.infrastructure.persistence.jpa.repository.InventoryJPARepository;
import com.learning.inventoryservice.infrastructure.persistence.mapper.InventoryEntityMapper;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class InventoryRepositoryAdapterTest {

    @Test
    void throwsInventoryNotFoundWhenProductHasNoInventory() {
        UUID productId = UUID.randomUUID();
        InventoryJPARepository repository = mock(InventoryJPARepository.class);
        InventoryEntityMapper mapper = mock(InventoryEntityMapper.class);
        when(repository.findByProductId(productId)).thenReturn(Optional.empty());
        InventoryRepositoryAdapter adapter = new InventoryRepositoryAdapter(repository, mapper);

        InventoryNotFoundException exception = assertThrows(InventoryNotFoundException.class,
                () -> adapter.findByProductId(new ProductId(productId)));

        assertEquals("Inventory not found for product: " + productId, exception.getMessage());
        verifyNoInteractions(mapper);
    }
}