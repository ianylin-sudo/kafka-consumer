package com.example.kafka_consumer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TopicNotFoundException extends RuntimeException {
  public TopicNotFoundException(String topic) {
    super("Topic not found: " + topic);
  }
}
