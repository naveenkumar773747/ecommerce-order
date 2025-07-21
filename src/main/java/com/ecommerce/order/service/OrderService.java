package com.ecommerce.order.service;

import com.ecommerce.order.mapper.OrderMapper;
import com.ecommerce.order.mapper.PaymentMapper;
import com.ecommerce.order.model.OrderRequest;
import com.ecommerce.order.producer.OrderProducer;
import com.ecommerce.order.producer.PaymentProducer;
import com.ecommerce.order.repository.CartRepository;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.shared.events.OrderEvent;
import com.ecommerce.shared.model.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

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
    private PaymentProducer paymentProducer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private PaymentMapper paymentMapper;

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

                    Order order = orderMapper.getOrderFromCartAndOrderRequest(cart, request, userId);

                    OrderEvent orderEvent = orderMapper.mapOrderToOrderEvent(order);

                    String jsonOrderEvent = null;
                    try {
                        jsonOrderEvent = objectMapper.writeValueAsString(orderEvent);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }

                    return orderProducer.sendMessage(jsonOrderEvent)
                            .flatMap(string -> orderRepository.save(order))
                            .doOnSuccess(saved -> {
                                cart.setItems(new ArrayList<>());
                                cartRepository.save(cart).subscribe();
                                log.info("Order placed successfully for userId : {} having orderId : {}", userId, order.getOrderId());
                            })
                            .flatMap(placedOrder ->
                                    Mono.just(paymentMapper.mapOrderToPaymentEvent(placedOrder))
                                            .flatMap(paymentEvent -> {
                                                String jsonPaymentEvent = null;
                                                try {
                                                    jsonPaymentEvent = objectMapper.writeValueAsString(paymentEvent);
                                                } catch (JsonProcessingException e) {
                                                    throw new RuntimeException(e);
                                                }
                                                return paymentProducer.sendMessage(jsonPaymentEvent)
                                                        .map(string -> order);
                                            }));

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