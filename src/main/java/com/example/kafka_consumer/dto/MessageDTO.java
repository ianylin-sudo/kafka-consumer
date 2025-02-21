package com.example.kafka_consumer.dto;

import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageDTO {
  private String message;
  private String key;
  private String topic;
  private int partition;
  private long offset;
  private Instant receivedAt;
}
