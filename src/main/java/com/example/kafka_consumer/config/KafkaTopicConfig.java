package com.example.kafka_consumer.config;

import java.util.Set;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.kafka")
public class KafkaTopicConfig {
  private Set<String> topics;
}
