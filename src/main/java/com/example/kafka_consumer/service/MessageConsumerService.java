package com.example.kafka_consumer.service;

import com.example.kafka_consumer.dto.MessageDTO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.ReceiverRecord;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageConsumerService {
    private static final int CACHE_SIZE = 10;
    private final ReactiveKafkaConsumerTemplate<String, String> kafkaTemplate;
    private final List<MessageDTO> messageCache = Collections.synchronizedList(new ArrayList<>());

    @PostConstruct
    public void consume() {
        kafkaTemplate
                .receive()
                .doOnNext(record -> {
                    MessageDTO message = MessageDTO.builder()
                            .message(record.value())
                            .key(record.key())
                            .partition(record.partition())
                            .offset(record.offset())
                            .receivedAt(Instant.now())
                            .build();

                    addToCache(message);
                    log.info("Received message: {} from partition: {}",
                            record.value(),
                            record.partition());
                })
                .doOnError(error ->
                        log.error("Error while consuming : {}", error.getMessage()))
                .subscribe(ReceiverRecord::receiverOffset);
    }

    private synchronized void addToCache(MessageDTO message) {
        messageCache.add(0, message);
        if (messageCache.size() > CACHE_SIZE) {
            messageCache.remove(CACHE_SIZE);
        }
    }

    public Mono<MessageDTO> getLatestMessage() {
        return messageCache.isEmpty()
                ? Mono.empty()
                : Mono.just(messageCache.get(0));
    }

    public Flux<MessageDTO> getLastMessages() {
        return Flux.fromIterable(messageCache);
    }
}
