package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.Dto.BookDto;
import org.example.Dto.UserDto;
import org.example.config.KafkaProducerConfig;
import org.example.config.KafkaTopicConfig;
import org.example.entity.BookEntity;
import org.example.request.BookCreateRequest;
import org.example.request.BookPutRequest;
import org.example.security.WebSecurityConfig;
import org.example.service.KafkaProducerService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {
        Application.class,
        WebSecurityConfig.class,
        KafkaProducerConfig.class
    },
    properties = {
        "spring.flyway.enabled=false",
        "topic-to-send-message=audit"
    }
)
@Import(EndToEndTest.ObjectMapperTestConfig.class)
@ActiveProfiles("test")
public class EndToEndTest {
  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Container
  @ServiceConnection
  public static final KafkaContainer KAFKA =
      new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

  @BeforeAll
  static void setup() {
    KAFKA.start();
  }

  @DynamicPropertySource
  static void kafkaProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
  }

  @Test
  @DisplayName("Тест всей логики приложения")
  public void E2ETest() {
    HttpEntity<UserDto> user1 = new HttpEntity<>(new UserDto(1L, "User1", "User1ov", new ArrayList<>()));
    HttpEntity<UserDto> user2 = new HttpEntity<>(new UserDto(2L, "User2", "User2ov", new ArrayList<>()));;
    ResponseEntity<String> createUserResponse1 =
        restTemplate.postForEntity("http://localhost:" + port + "/api/users/", user1, String.class);
    ResponseEntity<String> createUserResponse2 =
        restTemplate.postForEntity("http://localhost:" + port + "/api/users/", user2, String.class);
    assertEquals(HttpStatus.CREATED, createUserResponse1.getStatusCode());
    assertEquals(HttpStatus.CREATED, createUserResponse2.getStatusCode());
    assertEquals("1", createUserResponse1.getBody());
    assertEquals("2", createUserResponse2.getBody());

    ResponseEntity<UserDto> getUserResponse =
        restTemplate.getForEntity("http://localhost:" + port + "/api/users/1", UserDto.class);
    assertEquals(HttpStatus.OK, getUserResponse.getStatusCode());
    assertEquals(user1, getUserResponse.getBody());

    BookCreateRequest bookEntity = new BookCreateRequest(1L, "BookOfUser1", 1L);
    ResponseEntity<String> createBookResponse =
        restTemplate.postForEntity("http://localhost:" + port + "/api/books/", bookEntity, String.class);
    assertEquals(HttpStatus.CREATED, createBookResponse.getStatusCode());
    assertEquals("1", createBookResponse.getBody());

    ResponseEntity<BookDto> getBook1DataResponse =
        restTemplate.getForEntity("http://localhost:" + port + "/api/books/1", BookDto.class);
    assertEquals(HttpStatus.OK, getBook1DataResponse.getStatusCode());
    assertEquals(bookEntity.getId(), getBook1DataResponse.getBody().getId());

    ResponseEntity<UserDto> getUser1DataResponse =
        restTemplate.getForEntity("http://localhost:" + port + "/api/users/1", UserDto.class);
    assertEquals(HttpStatus.OK, getUser1DataResponse.getStatusCode());
    assertEquals(bookEntity.getId(), getUser1DataResponse.getBody().getBooks().get(0));

    BookPutRequest newBookRequest = new BookPutRequest(1L, "BookOfUser2", 2L);
    BookDto updatedBook = new BookDto(1L, "BookOfUser2", 2L);

    restTemplate.put("http://localhost:" + port + "/api/books/1", newBookRequest, BookEntity.class);
    ResponseEntity<BookDto> putBookResponse =
        restTemplate.getForEntity("http://localhost:" + port + "/api/books/1", BookDto.class);
    assertEquals(updatedBook, putBookResponse.getBody());

    ResponseEntity<UserDto> getUser2DataResponse =
        restTemplate.getForEntity("http://localhost:" + port + "/api/users/2", UserDto.class);
    assertEquals(HttpStatus.OK, getUser2DataResponse.getStatusCode());
    assertEquals(updatedBook.getId(), getUser2DataResponse.getBody().getBooks().get(0));

    restTemplate.delete("http://localhost:" + port + "/api/books/1", updatedBook);
    getUser2DataResponse =
        restTemplate.getForEntity("http://localhost:" + port + "/api/users/2", UserDto.class);
    assertEquals(new ArrayList<>(), getUser2DataResponse.getBody().getBooks());
  }

  @TestConfiguration
  static class ObjectMapperTestConfig {
    @Bean
    public ObjectMapper objectMapper() {
      return new ObjectMapper().registerModule(new JavaTimeModule());
    }
  }
}