package com.example.kafka_consumer.service;

import com.example.kafka_consumer.config.KafkaTopicConfig;
import com.example.kafka_consumer.dto.MessageDTO;
import com.example.kafka_consumer.exception.TopicNotFoundException;
import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageConsumerService {
  private static final int CACHE_SIZE = 10;
  private final ReactiveKafkaConsumerTemplate<String, String> kafkaTemplate;
  private final KafkaTopicConfig topicConfig;
  private final Map<String, List<MessageDTO>> messageCache = new ConcurrentHashMap<>();

  @PostConstruct
  public void consume() {
    // Initialize cache for all topics
    topicConfig.getTopics().forEach(topic -> messageCache.put(topic, new ArrayList<>()));

    kafkaTemplate
        .receive()
        .doOnNext(
            record -> {
              MessageDTO message =
                  MessageDTO.builder()
                      .message(record.value())
                      .key(record.key())
                      .topic(record.topic())
                      .partition(record.partition())
                      .offset(record.offset())
                      .receivedAt(Instant.now())
                      .build();

              addToCache(message);
              log.info(
                  "Received message: {} from topic: {} partition: {}",
                  record.value(),
                  record.topic(),
                  record.partition());
            })
        .doOnError(error -> log.error("Error while consuming : {}", error.getMessage()))
        .subscribe(ReceiverRecord::receiverOffset);
  }

  private synchronized void addToCache(MessageDTO message) {
    List<MessageDTO> topicMessages = messageCache.get(message.getTopic());
    topicMessages.add(0, message);
    if (topicMessages.size() > CACHE_SIZE) {
      topicMessages.remove(CACHE_SIZE);
    }
  }

  public Mono<MessageDTO> getLatestMessage(String topic) {
    validateTopic(topic);
    List<MessageDTO> messages = messageCache.get(topic);
    return messages.isEmpty() ? Mono.empty() : Mono.just(messages.get(0));
  }

  public Flux<MessageDTO> getLastMessages(String topic) {
    validateTopic(topic);
    return Flux.fromIterable(messageCache.get(topic));
  }

  private void validateTopic(String topic) {
    if (!topicConfig.getTopics().contains(topic)) {
      throw new TopicNotFoundException(topic);
    }
  }
}
