package com.ecommerce.order.service;

import com.ecommerce.order.mapper.OrderMapper;
import com.ecommerce.order.model.OrderRequest;
import com.ecommerce.order.producer.OrderProducer;
import com.ecommerce.order.repository.CartRepository;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.shared.enums.DeliveryTypeEnum;
import com.ecommerce.shared.enums.OrderStatusEnum;
import com.ecommerce.shared.events.OrderEvent;
import com.ecommerce.shared.model.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderProducer orderProducer;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * This method takes OrderRequest and userId for placing a new order.
     *
     * @param userId  : userId of a user
     * @param request : OrderRequest details
     * @return Order mono : with order confirmation
     */
    public Mono<Order> placeOrder(String userId, OrderRequest request) {
        return cartRepository.findByUserId(userId)
                .flatMap(cart -> {
                    Order order = new Order();
                    order.setOrderId(UUID.randomUUID().toString().split("-")[0]);
                    order.setUserId(userId);
                    order.setItems(cart.getItems());
                    order.setTotalAmount(cart.calculateTotal() + (request.getDeliveryType().equals(DeliveryTypeEnum.EXPRESS) ? 25 : 0));
                    order.setStatus(OrderStatusEnum.PLACED);
                    order.setCreatedDateTime(LocalDateTime.now().toString());
                    order.setDeliveryInfo(request.getDeliveryInfo());
                    order.setBillingInfo(request.getBillingInfo());
                    order.setDeliveryType(request.getDeliveryType());

                    OrderEvent event = orderMapper.mapOrderToEvent(order);

                    ObjectMapper objectMapper = new ObjectMapper();
                    objectMapper.registerModule(new JavaTimeModule());
                    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                    String jsonEvent = null;
                    try {
                        jsonEvent = objectMapper.writeValueAsString(event);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }

                    return orderProducer.sendMessage(jsonEvent)
                            .flatMap(string -> orderRepository.save(order))
                            .doOnSuccess(saved -> {
                                cart.setItems(new ArrayList<>());
                                cartRepository.save(cart).subscribe();
                                log.info("Order placed successfully for userId : {} having orderId : {}", userId, order.getOrderId());
                            });
                });
    }

    /**
     * This method takes userId to retrieve all the orders of a user
     *
     * @param userId : userId of a user
     * @return Order flux : with all the orders
     */
    public Flux<Order> getOrdersByUser(String userId) {
        return orderRepository.findOrdersByUserId(userId);
    }

    /**
     * This method takes orderId to retrieve the order details
     *
     * @param orderId : orderId of an order
     * @return Order mono : with Order details
     */
    public Mono<Order> getOrderById(String orderId) {
        return orderRepository.findById(orderId);
    }
}