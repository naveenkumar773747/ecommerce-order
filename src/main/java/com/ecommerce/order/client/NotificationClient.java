package com.ecommerce.order.client;

import com.ecommerce.shared.events.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class NotificationClient {

    private static final Logger log = LoggerFactory.getLogger(NotificationClient.class);

    private final WebClient webClient;

    public NotificationClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://NOTIFICATION-SERVICE").build();
    }

    public Mono<String> sendNotification(NotificationEvent event) {
        log.info("Notification service call started via WebClient for order id: {}", event.getOrderId());
        return webClient.post()
                .uri("/api/notification")
                .bodyValue(event)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(res -> log.info("Notification sent successfully: {}", res))
                .onErrorResume(ex -> {
                    log.info("Notification WebClient failed: {}", ex.getMessage());
                    return Mono.just("Notification failed for orderId: " + event.getOrderId());
                });
    }
}
