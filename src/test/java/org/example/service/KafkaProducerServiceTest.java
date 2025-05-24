package org.example.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.Application;
import org.example.Dto.MessageDto;
import org.example.config.AppConfig;
import org.example.config.KafkaTopicConfig;
import org.example.enums.Action;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(
    classes = {
        KafkaProducerService.class,
    },
    properties = {"topic-to-send-message=audit"}
)
@Import({KafkaAutoConfiguration.class, KafkaProducerServiceTest.ObjectMapperTestConfig.class})
@Testcontainers
class KafkaProducerServiceTest {
  @TestConfiguration
  static class ObjectMapperTestConfig {
    @Bean
    public ObjectMapper objectMapper() {
      return new ObjectMapper().registerModule(new JavaTimeModule());
    }
  }

  @Container
  @ServiceConnection
  public static final KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

  @Autowired
  private KafkaProducerService kafkaProducerService;
  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldSendMessageToKafkaPositive() {
    MessageDto testDtoMessage = new MessageDto(1L, Instant.now(), "Type1", "Log1");

    assertDoesNotThrow(() -> kafkaProducerService.sendMessage(testDtoMessage));

    KafkaTestConsumer consumer = new KafkaTestConsumer(KAFKA.getBootstrapServers(), "audit-group");
    consumer.subscribe(List.of("audit"));

    ConsumerRecords<String, String> userAudits = consumer.poll();
    assertEquals(1, userAudits.count());
    userAudits.iterator().forEachRemaining(
        userAudit -> {
          MessageDto message = null;
          try {
            message = objectMapper.readValue(userAudit.value(), MessageDto.class);
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
          assertEquals(testDtoMessage, message);
        }
    );
  }


  @Test
  void shouldSendMessageToKafkaNegative() {
    String largeText = new String(new byte[1_000_001]);
    assertThrows(KafkaException.class, () -> {
      kafkaProducerService.sendMessage(new MessageDto(1L, Instant.now(), Action.INSERT.toString(), largeText));
    });
  }
}

class KafkaTestConsumer {
  private final KafkaConsumer<String, String> consumer;

  public KafkaTestConsumer(String bootstrapServers, String groupId) {
    Properties props = new Properties();

    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    this.consumer = new KafkaConsumer<>(props);
  }

  public void subscribe(List<String> topics) {
    consumer.subscribe(topics);
  }

  public ConsumerRecords<String, String> poll() {
    return consumer.poll(Duration.ofSeconds(5));
  }

}
