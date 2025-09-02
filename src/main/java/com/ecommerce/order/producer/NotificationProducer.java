package com.ecommerce.order.producer;

import com.ecommerce.order.client.NotificationClient;
import com.ecommerce.order.util.JsonUtil;
import com.ecommerce.shared.events.NotificationEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;


@Component
public class NotificationProducer {

    private static final Logger log = LoggerFactory.getLogger(NotificationProducer.class);

    @Value("${spring.kafka.topic.notification}")
    private String topic;

    @Autowired
    private KafkaSender<String, String> kafkaSender;

    @Autowired
    private JsonUtil jsonUtil;

    @Autowired
    private NotificationClient notificationClient;

    /**
     * This method takes message as a string format of OrderEvent and publishes to kafka topic.
     *
     * @param notificationEvent : String format of OrderEvent
     * @return String mono : OrderEvent string
     */
    public Mono<String> sendMessage(NotificationEvent notificationEvent) {
        return jsonUtil.toJson(notificationEvent)
                .map(message -> {
                    ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
                    return SenderRecord.create(record, message);
                })
                .flatMap(senderRecord ->
                        kafkaSender.send(Mono.just(senderRecord))
                                .next()
                                .map(SenderResult::correlationMetadata)
                                .map(meta -> "Message sent with metadata: " + meta)
                                .doOnNext(info -> log.info("Published Notification Event message to topic : {} : {}", topic, senderRecord.value()))
                                .onErrorResume(ex -> {
                                    log.error("Kafka publish failed, falling back to WebClient call", ex);
                                    return notificationClient.sendNotification(notificationEvent);
                                })
                );
    }
}
