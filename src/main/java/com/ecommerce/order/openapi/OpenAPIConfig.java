package com.ecommerce.order.openapi;

import com.ecommerce.order.model.OrderRequest;
import com.ecommerce.shared.model.*;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        Components components = new Components();

        Class<?>[] schemaClasses = {
                OrderRequest.class,
                UserInfo.class,
                DeliveryInfo.class,
                BillingInfo.class,
                Cart.class,
                CartItem.class,
                Order.class
        };

        for (Class<?> clazz : schemaClasses) {
            Map<String, Schema> schemaMap = ModelConverters.getInstance().read(clazz);
            for (Map.Entry<String, Schema> entry : schemaMap.entrySet()) {
                components.addSchemas(entry.getKey(), entry.getValue());
            }
        }

        return new OpenAPI()
                .info(new Info().title("Ordering System API")
                        .version("1.0.0")
                        .description("API docs for the ordering system"))
                .components(components)
                .paths(new Paths()

                        // Add to cart
                        .addPathItem("/cart/{userId}/items", new PathItem().post(new Operation()
                                .operationId("addToCart")
                                .summary("Add item to cart")
                                .addParametersItem(new Parameter()
                                        .name("userId").in("path").required(true).schema(new StringSchema().example("1")))
                                .requestBody(new RequestBody()
                                        .required(true)
                                        .content(new Content().addMediaType("application/json",
                                                new MediaType().schema(new Schema<>().$ref("#/components/schemas/CartItem")))))
                                .responses(new ApiResponses()
                                        .addApiResponse("200", new ApiResponse().description("Item added to cart")
                                                .content(new Content().addMediaType("application/json",
                                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/Cart"))))))))

                        // View cart
                        .addPathItem("/cart/{userId}", new PathItem().get(new Operation()
                                .operationId("getCart")
                                .summary("View cart contents")
                                .addParametersItem(new Parameter()
                                        .name("userId").in("path").required(true).schema(new StringSchema().example("1")))
                                .responses(new ApiResponses()
                                        .addApiResponse("200", new ApiResponse().description("Cart retrieved")
                                                .content(new Content().addMediaType("application/json",
                                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/Cart"))))))))

                        // Update cart item
                        .addPathItem("/cart/{userId}/update/{productId}", new PathItem().put(new Operation()
                                .operationId("updateCartItem")
                                .summary("Update item quantity in cart")
                                .addParametersItem(new Parameter()
                                        .name("userId").in("path").required(true).schema(new StringSchema()))
                                .addParametersItem(new Parameter()
                                        .name("productId").in("path").required(true).schema(new StringSchema()))
                                .requestBody(new RequestBody()
                                        .required(true)
                                        .content(new Content().addMediaType("application/json",
                                                new MediaType().schema(new Schema<>().$ref("#/components/schemas/Cart")))))
                                .responses(new ApiResponses()
                                        .addApiResponse("200", new ApiResponse().description("Item quantity updated")
                                                .content(new Content().addMediaType("application/json",
                                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/Cart"))))))))

                        // Remove item from cart
                        .addPathItem("/cart/{userId}/remove/{productId}", new PathItem().delete(new Operation()
                                .operationId("removeCartItem")
                                .summary("Remove item from cart")
                                .addParametersItem(new Parameter()
                                        .name("userId").in("path").required(true).schema(new StringSchema()))
                                .addParametersItem(new Parameter()
                                        .name("productId").in("path").required(true).schema(new StringSchema()))
                                .responses(new ApiResponses()
                                        .addApiResponse("200", new ApiResponse().description("Item removed from cart")
                                                .content(new Content().addMediaType("application/json",
                                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/Cart"))))))))

                        // Place order
                        .addPathItem("/order/{userId}", new PathItem().post(new Operation()
                                .operationId("placeOrder")
                                .summary("Place an order")
                                .addParametersItem(new Parameter()
                                        .name("userId").in("path").required(true).schema(new StringSchema().example("1")))
                                .requestBody(new RequestBody()
                                        .required(true)
                                        .content(new Content().addMediaType("application/json",
                                                new MediaType().schema(new Schema<>().$ref("#/components/schemas/OrderRequest")))))
                                .responses(new ApiResponses()
                                        .addApiResponse("201", new ApiResponse().description("Order placed successfully")
                                                .content(new Content().addMediaType("application/json",
                                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/Order")))))
                                        .addApiResponse("400", new ApiResponse().description("Invalid address or billing info")))))

                        // Get order by ID
                        .addPathItem("/orders/{orderId}", new PathItem().get(new Operation()
                                .operationId("getOrderById")
                                .summary("Get order by ID")
                                .addParametersItem(new Parameter()
                                        .name("orderId").in("path").required(true).schema(new StringSchema().example("ORD123456")))
                                .responses(new ApiResponses()
                                        .addApiResponse("200", new ApiResponse().description("Order details retrieved")
                                                .content(new Content().addMediaType("application/json",
                                                        new MediaType().schema(new Schema<>().$ref("#/components/schemas/Order")))))
                                        .addApiResponse("404", new ApiResponse().description("Order not found")))))

                );
    }
}
