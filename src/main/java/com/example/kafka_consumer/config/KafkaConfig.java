package com.example.kafka_consumer.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class KafkaConfig {
    private final KafkaProperties kafkaProperties;

    @Bean
    public ReactiveKafkaConsumerTemplate<String, String> reactiveKafkaConsumerTemplate() {
        ReceiverOptions<String, String> receiverOptions =
                ReceiverOptions
                        .<String, String>create(kafkaProperties.buildConsumerProperties())
                        .subscription(Collections.singletonList("test-topic"));

        return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
    }
}
