package com.sachet.expirationservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sachet.expirationservice.model.OrderCreatedEventModel;
import com.sachet.expirationservice.repository.ExpirationRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;

import static java.time.temporal.ChronoUnit.MILLIS;

@Component
@Slf4j
public class OrderCreatedConsumer implements AcknowledgingMessageListener<String, String> {
    private final ObjectMapper objectMapper;
    private final ExpirationRepository expirationRepository;

    private final String orderCreatedQue = "order-created";

    public OrderCreatedConsumer(ObjectMapper objectMapper, ExpirationRepository expirationRepository) {
        this.objectMapper = objectMapper;
        this.expirationRepository = expirationRepository;
    }

    @KafkaListener(topics = "${spring.kafka.topicconsume}")
    @Override
    public void onMessage(ConsumerRecord<String, String> consumerRecord, Acknowledgment acknowledgment) {
        try {
            var order = objectMapper.readValue(consumerRecord.value(), OrderCreatedEventModel.class);
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", new Locale("us"));
            var expDate = sdf.parse(order.getExpiresAt());
            var date = new Date();
            var expMillis = expDate.getTime() - date.getTime();
            log.info("The order: {} will expire after: {}", order, expMillis);
            order.setExpTime(expMillis);
            order = expirationRepository.save(order);
            log.info("Saved to redis: {}", order);
            assert acknowledgment != null;
            acknowledgment.acknowledge();
        } catch (JsonProcessingException | ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
