package org.example;

import lombok.extern.slf4j.Slf4j;
import org.example.security.WebSecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {Application.class, WebSecurityConfig.class})
@ActiveProfiles("test")
@Slf4j
@Testcontainers
public class PostgreSQLTest {
  @Container
  private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
      .withDatabaseName("testdb")
      .withUsername("admin")
      .withPassword("admin")
      .withInitScript("init.sql");

  @BeforeAll
  public static void init(){
    log.info("PostgreSQL контейнер хост: {}", postgres.getHost() + ":" + postgres.getFirstMappedPort());
    log.info("PostgreSQL URL соединения: {}", postgres.getJdbcUrl());
    postgres.start();
  }

  @Test
  public void testDataBase() {
    assertTrue(postgres.isRunning());
  }
}
