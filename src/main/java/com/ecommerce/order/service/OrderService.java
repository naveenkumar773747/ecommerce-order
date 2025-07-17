package com.ecommerce.order.service;

import com.ecommerce.order.model.OrderRequest;
import com.ecommerce.order.producer.OrderProducer;
import com.ecommerce.order.repository.CartRepository;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.shared.enums.OrderStatusEnum;
import com.ecommerce.shared.events.OrderPlacedEvent;
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

    public Mono<Order> placeOrder(String userId, OrderRequest request) {
        return cartRepository.findByUserId(userId)
                .flatMap(cart -> {
                    Order order = new Order();
                    order.setOrderId(UUID.randomUUID().toString().split("-")[0]);
                    order.setUserId(userId);
                    order.setItems(cart.getItems());
                    order.setTotalAmount(cart.calculateTotal());
                    order.setStatus(OrderStatusEnum.PENDING);
                    order.setCreatedDateTime(LocalDateTime.now().toString());
                    order.setDeliveryInfo(request.getDeliveryInfo());
                    order.setBillingInfo(request.getBillingInfo());


                    OrderPlacedEvent event = new OrderPlacedEvent();
                    event.setOrderId(order.getOrderId());
                    event.setUserId(order.getUserId());
                    event.setItems(order.getItems());
                    event.setTotalAmount(order.getTotalAmount());
                    event.setDeliveryInfo(order.getDeliveryInfo());
                    event.setBillingInfo(order.getBillingInfo());
                    event.setCreatedDateTime(order.getCreatedDateTime());

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

    public Flux<Order> getOrdersByUser(String userId) {
        return orderRepository.findOrdersByUserId(userId);
    }

    public Mono<Order> getOrderById(String orderId) {
        return orderRepository.findById(orderId);
    }
}