package com.ecommerce.order.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Ordering System API")
                        .version("1.0")
                        .description("API docs for the ordering system"))
                .paths(new Paths()

                        // Add to cart
                        .addPathItem("/cart/add", new PathItem().post(new Operation()
                                .operationId("addToCart")
                                .summary("Add item to cart")
                                .responses(new ApiResponses()
                                        .addApiResponse("200", new ApiResponse().description("Item added to cart")))))

                        // Get cart
                        .addPathItem("/cart", new PathItem().get(new Operation()
                                .operationId("getCart")
                                .summary("View cart contents")
                                .responses(new ApiResponses()
                                        .addApiResponse("200", new ApiResponse().description("Cart retrieved")))))

                        // Update cart item
                        .addPathItem("/cart/update/{itemId}", new PathItem().put(new Operation()
                                .operationId("updateCartItem")
                                .summary("Update item quantity")
                                .addParametersItem(new Parameter()
                                        .name("itemId").in("path").required(true).schema(new StringSchema()))
                                .responses(new ApiResponses()
                                        .addApiResponse("200", new ApiResponse().description("Item updated")))))

                        // Remove item from cart
                        .addPathItem("/cart/remove/{itemId}", new PathItem().delete(new Operation()
                                .operationId("removeCartItem")
                                .summary("Remove item from cart")
                                .addParametersItem(new Parameter()
                                        .name("itemId").in("path").required(true).schema(new StringSchema()))
                                .responses(new ApiResponses()
                                        .addApiResponse("200", new ApiResponse().description("Item removed from cart")))))

                        // Place order
                        .addPathItem("/order/place", new PathItem().post(new Operation()
                                .operationId("placeOrder")
                                .summary("Place an order")
                                .responses(new ApiResponses()
                                        .addApiResponse("200", new ApiResponse().description("Order placed successfully")))))

                        // Get order by ID
                        .addPathItem("/order/{orderId}", new PathItem().get(new Operation()
                                .operationId("getOrderById")
                                .summary("Get order by ID")
                                .addParametersItem(new Parameter()
                                        .name("orderId").in("path").required(true).schema(new StringSchema()))
                                .responses(new ApiResponses()
                                        .addApiResponse("200", new ApiResponse().description("Order details retrieved")))))
                );
    }
}
