package com.learning.orderplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.learning.orderplatform"})
public class OrderPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderPlatformApplication.class, args);
    }

}
