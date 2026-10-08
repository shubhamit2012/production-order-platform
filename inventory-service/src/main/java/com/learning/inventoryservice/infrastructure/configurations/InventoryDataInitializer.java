package com.learning.inventoryservice.infrastructure.configurations;

import com.learning.inventoryservice.application.port.out.InventoryRepository;
import com.learning.inventoryservice.domain.Inventory;
import com.learning.inventoryservice.domain.InventoryId;
import com.learning.inventoryservice.domain.ProductId;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class InventoryDataInitializer {

    @Bean
    CommandLineRunner seedInventory(InventoryRepository inventoryRepository) {
        return args -> {

            inventoryRepository.save(
                    new Inventory(
                            new InventoryId(UUID.randomUUID()),
                            new ProductId(UUID.fromString("11111111-1111-1111-1111-111111111111")),
                            0,
                            100
                    )
            );

            inventoryRepository.save(
                    new Inventory(
                            new InventoryId(UUID.randomUUID()),
                            new ProductId(UUID.fromString("22222222-2222-2222-2222-222222222222")),
                            0,
                            100
                    )
            );
        };
    }
}