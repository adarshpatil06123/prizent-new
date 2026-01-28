package com.elowen.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("identity-service", r -> r.path("/api/auth/**")
                        .uri("http://localhost:8081"))
                .route("admin-service-brands", r -> r.path("/api/admin/brands/**")
                        .uri("http://localhost:8082"))
                .route("product-service", r -> r.path("/api/products/**")
                        .uri("http://localhost:8083"))
                .route("pricing-service", r -> r.path("/api/pricing/**")
                        .uri("http://localhost:8084"))
                .build();
    }
}