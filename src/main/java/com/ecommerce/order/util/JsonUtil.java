package com.ecommerce.order.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class JsonUtil {

    private final ObjectMapper objectMapper;

    @Autowired
    public JsonUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Mono<String> toJson(Object obj) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(obj))
                .onErrorMap(JsonProcessingException.class,
                        ex -> new RuntimeException("Serialization failed", ex));
    }
}
