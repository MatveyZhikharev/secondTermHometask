package org.example.service;

import jakarta.validation.ConstraintViolationException;
import org.example.Application;
import org.example.config.KafkaProducerConfig;
import org.example.entity.OutboxRecord;
import org.example.repository.OutboxRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(
    properties = {
        "spring.scheduling.enabled=false",
        "spring.flyway.enabled=false",
        "topic-to-send-message=audit"
    },
    classes = {
        Application.class,
        KafkaProducerConfig.class
    }
    )
@Testcontainers
public class OutboxSchedulerTest {
  @ServiceConnection
  @Container
  public static final KafkaContainer KAFKA =
      new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

  @Container
  static PostgreSQLContainer<?> postgresContainer =
      new PostgreSQLContainer<>("postgres:17")
          .withInitScript("init.sql")
          .withDatabaseName("admin")
          .withUsername("admin");

  static {
    postgresContainer.start();
    KAFKA.start();
  }

  @Autowired
  private OutboxScheduler outboxScheduler;
  @Autowired
  private OutboxRepository outboxRepository;

  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);

    registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgresContainer::getUsername);
    registry.add("spring.datasource.password", postgresContainer::getPassword);
  }

  @BeforeEach
  void cleanUp() {
    outboxRepository.deleteAll();
    outboxScheduler.processOutbox();
  }

  @Test
  void testOutboxSchedulerPositive() {
    OutboxRecord record1 = new OutboxRecord("Record1");
    OutboxRecord record2 = new OutboxRecord("Record2");
    outboxRepository.save(record1);
    outboxRepository.save(record2);

    assertEquals(2, outboxRepository.findAll().size());

    outboxScheduler.processOutbox();
    assertTrue(outboxRepository.findAll().isEmpty());
  }

  @Test
  void testOutboxSchedulerNegative() {
    assertThrows(ConstraintViolationException.class,
        () -> outboxRepository.save(new OutboxRecord(null)));
  }
}