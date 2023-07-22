package com.sachet.expirationservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sachet.expirationservice.model.OrderCancelledEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class OrderCancelledEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.expireorder}")
    private String topic;

    public OrderCancelledEventPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public CompletableFuture<SendResult<String, String>> sendOrderCancelEvent(
            OrderCancelledEvent event
    ) throws JsonProcessingException {
        log.info("Sending Message For Order Expired Event: {}", event);
        var key = event.getOrderId();
        var value = objectMapper.writeValueAsString(event);
        var productRecord = new ProducerRecord<>(topic, key, value);
        return kafkaTemplate.send(productRecord)
                .whenComplete(((result, throwable) -> {
                    if (throwable != null) {
                        handleFailure(key, value, throwable.getMessage());
                    } else {
                        handleSuccess(key, value, result);
                    }
                }));
    }

    private void handleSuccess(String key, String value, SendResult<String, String> result) {
        log.info("Successfully sent the event to kafka, with KEY: {}, VALUE: {}",
                key, result.getProducerRecord().value());
    }

    private void handleFailure(String key, String value, String message) {
        log.info("Unable to send message to Kafka, failed: {}", message);
    }
}
