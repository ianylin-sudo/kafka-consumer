package com.example.kafka_consumer.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class MessageDTO {
    private String message;
    private String key;
    private int partition;
    private long offset;
    private Instant receivedAt;
}
