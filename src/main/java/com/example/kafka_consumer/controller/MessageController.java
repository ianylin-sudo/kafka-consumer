package com.example.kafka_consumer.controller;

import com.example.kafka_consumer.dto.MessageDTO;
import com.example.kafka_consumer.service.MessageConsumerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
  private final MessageConsumerService consumerService;

  @GetMapping("/{topic}/latest")
  public Mono<MessageDTO> getLatestMessage(@PathVariable String topic) {
    return consumerService.getLatestMessage(topic);
  }

  @GetMapping("/{topic}/recent")
  public Flux<MessageDTO> getRecentMessages(@PathVariable String topic) {
    return consumerService.getLastMessages(topic);
  }
}
