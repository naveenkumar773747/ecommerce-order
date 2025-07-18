package com.ecommerce.order.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

@Slf4j
@Service
public class UserProducer {

    @Value("${spring.kafka.topic.user}")
    private String topic;

    @Autowired
    private KafkaSender<String, String> kafkaSender;

    /**
     * This method takes message as a string format of UserInfo and publishes to kafka topic.
     *
     * @param message : String format of UserInfo
     * @return String mono : UserInfo string
     */
    public Mono<String> sendMessage(String message) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
        SenderRecord<String, String, String> senderRecord = SenderRecord.create(record, message);

        return kafkaSender.send(Mono.just(senderRecord))
                .next()
                .map(SenderResult::correlationMetadata)
                .map(meta -> "Message sent with metadata: " + meta)
                .onErrorReturn("Failed to send message");
    }
}
