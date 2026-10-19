package com.learning.orderservice.infrastructure.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class InventoryClientConfiguration {

    @Bean
    RestClient inventoryRestClient(RestClient.Builder builder,
            @Value("${inventory.service.base-url}") String baseUrl) {
        return builder
                .baseUrl(baseUrl)
                .build();
    }

}

