package com.ecommerce.order.router;

import com.ecommerce.order.handler.OrderHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class OrderRouter {

    @Bean
    public RouterFunction<ServerResponse> orderRoute(OrderHandler handler) {
        return RouterFunctions.route()
                .POST("/orders/{userId}", handler::placeOrder)
                .GET("/orders/{userId}", handler::getUserOrders)
                .GET("/orders/{orderId}", handler::getOrderById)
                .build();
    }
}