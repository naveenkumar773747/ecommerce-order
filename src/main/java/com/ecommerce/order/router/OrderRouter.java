package com.ecommerce.order.router;

import com.ecommerce.order.handler.OrderHandler;
import com.ecommerce.order.model.OrderRequest;
import com.ecommerce.shared.model.CartItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class OrderRouter {

    @Bean
    public RouterFunction<ServerResponse> orderRoute(OrderHandler handler) {
        return RouterFunctions.route()
                .POST("/cart/{userId}/items", handler::addItemToCart)
                .PUT("/cart/{userId}/items/{productId}", handler::updateCartItem)
                .DELETE("/cart/{userId}/items/{productId}", handler::removeCartItem)
                .GET("/cart/{userId}", handler::getCart)

                .POST("/orders/{userId}", handler::placeOrder)
                .GET("/orders/{userId}", handler::getUserOrders)
                .GET("/orders/{orderId}", handler::getOrderById)
                .build();
    }
}