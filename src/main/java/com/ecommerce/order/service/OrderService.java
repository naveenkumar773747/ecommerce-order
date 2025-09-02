package com.ecommerce.order.service;

import com.ecommerce.order.mapper.OrderMapper;
import com.ecommerce.order.mapper.PaymentMapper;
import com.ecommerce.order.model.OrderRequest;
import com.ecommerce.order.producer.NotificationProducer;
import com.ecommerce.order.producer.PaymentProducer;
import com.ecommerce.order.repository.CartRepository;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.order.util.JsonUtil;
import com.ecommerce.shared.events.NotificationEvent;
import com.ecommerce.shared.model.Order;
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
    private NotificationProducer notificationProducer;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PaymentProducer paymentProducer;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private JsonUtil jsonUtil;

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

                    NotificationEvent notificationEvent = orderMapper.mapOrderToNotificationEvent(order);

                    return notificationProducer.sendMessage(notificationEvent)
                            .flatMap(string -> orderRepository.save(order))
                            .doOnSuccess(saved -> {
                                cart.setItems(new ArrayList<>());
                                cartRepository.save(cart).subscribe();
                                log.info("Order placed successfully for userId : {} having orderId : {}", userId, order.getOrderId());
                            })
                            .flatMap(placedOrder -> Mono.just(paymentMapper.mapOrderToPaymentEvent(placedOrder))
                                    .flatMap(paymentEvent ->
                                            jsonUtil.toJson(paymentEvent)
                                                    .flatMap(jsonPaymentEvent ->
                                                            paymentProducer.sendMessage(jsonPaymentEvent)
                                                                    .map(string -> order))
                                    ));
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