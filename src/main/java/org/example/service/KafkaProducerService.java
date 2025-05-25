package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.Dto.MessageDto;
import org.example.entity.OutboxRecord;
import org.example.repository.OutboxRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {
  private final OutboxRepository outboxRepository;
  private final ObjectMapper objectMapper;
  private final String topic;

  public KafkaProducerService(OutboxRepository outboxRepository,
                              ObjectMapper objectMapper,
                              @Value("${topic-to-send-message}") String topic) {
    this.outboxRepository = outboxRepository;
    this.objectMapper = objectMapper;
    this.topic = topic;
  }

  public void sendMessage(MessageDto messageDto) {
    String message = null;
    try {
      message = objectMapper.writeValueAsString(messageDto);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    outboxRepository.save(new OutboxRecord(message));
  }
}

