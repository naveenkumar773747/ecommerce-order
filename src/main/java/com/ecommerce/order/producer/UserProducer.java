package com.ecommerce.order.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

@Slf4j
@Service
public class UserProducer {

    private static final String TOPIC = "user_topic";

    @Autowired
    private KafkaSender<String, String> kafkaSender;

    public Mono<String> sendMessage(String message) {
        ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, message);
        SenderRecord<String, String, String> senderRecord = SenderRecord.create(record, message);

        return kafkaSender.send(Mono.just(senderRecord))
                .next()
                .map(SenderResult::correlationMetadata)
                .map(meta -> "Message sent with metadata: " + meta)
                .onErrorReturn("Failed to send message");
    }
}
