package com.ecommerce.order.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;


@Service
public class OrderProducer {

    private static final Logger log = LoggerFactory.getLogger(OrderProducer.class);

    @Value("${spring.kafka.topic.order}")
    private String topic;

    @Autowired
    private KafkaSender<String, String> kafkaSender;

    public Mono<String> sendMessage(String message) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
        SenderRecord<String, String, String> senderRecord = SenderRecord.create(record, message);

        return kafkaSender.send(Mono.just(senderRecord))
                .next()
                .map(SenderResult::correlationMetadata)
                .map(meta -> "Message sent with metadata: " + meta)
                .doOnNext(info -> log.info("Published message to topic : {} : {}", topic, message))
                .onErrorReturn("Failed to send message");
    }
}
