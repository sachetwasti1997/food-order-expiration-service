package com.sachet.expirationservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class AutoCreateProducerConfig {

    @Value("${spring.kafka.expireorder}")
    private String expireordertopic;


    @Bean
    public NewTopic orderExpiredTopic() {
        return TopicBuilder
                .name(expireordertopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
