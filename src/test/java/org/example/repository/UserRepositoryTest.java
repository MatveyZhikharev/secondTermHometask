package org.example.repository;

import jakarta.validation.ConstraintViolationException;
import org.example.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(properties = {"spring.flyway.enabled=false"})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class UserRepositoryTest {
  @Container
  public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:13")
      .withInitScript("init.sql")
      .withDatabaseName("test database")
      .withUsername("admin");

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
  }

  static {
    POSTGRES.start();
  }

  @Autowired
  private UserRepository userRepository;

  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
  }

  @Test
  @DisplayName("Тест на сохранение и поиск пользователя по ID")
  @Transactional
  public void testSaveAndFindById() {
    UserEntity testUser = new UserEntity(null, "test", "user", new ArrayList<>());
    UserEntity savedUser = userRepository.save(testUser);

    Optional<UserEntity> foundUser = userRepository.findById(savedUser.getId());
    assertTrue(foundUser.isPresent());

    assertEquals(savedUser.getId(), foundUser.get().getId());
    assertEquals("test", foundUser.get().getName());
    assertEquals("user", foundUser.get().getSurname());
  }

  @Test
  @DisplayName("Тест на поиск несуществующего пользователя")
  public void testFindByIdAndNotFound() {
    Optional<UserEntity> foundUser = userRepository.findById(999L);
    assertFalse(foundUser.isPresent());
  }

  @Test
  @DisplayName("Тест на удаление пользователя")
  public void testDeleteUser() {
    UserEntity testUser = new UserEntity(null, "test", "user", new ArrayList<>());
    UserEntity savedUser = userRepository.save(testUser);

    userRepository.delete(savedUser);
    Optional<UserEntity> foundBook = userRepository.findById(savedUser.getId());
    assertFalse(foundBook.isPresent());
  }

  @Test
  @DisplayName("Тест на получение всех пользователей")
  public void testFindAll() {
    UserEntity testUser1 = new UserEntity(null, "test1", "user1", new ArrayList<>());
    UserEntity testUser2 = new UserEntity(null, "test2", "user2", new ArrayList<>());
    userRepository.save(testUser1);
    userRepository.save(testUser2);


    List<UserEntity> books = userRepository.findAll();
    assertEquals(2, books.size());
    assertTrue(books.stream().anyMatch(b -> b.getName().equals("test1")));
    assertTrue(books.stream().anyMatch(b -> b.getName().equals("test2")));
  }

  @Test
  @DisplayName("Тест на обновление пользователя")
  public void testUpdateUser() {
    UserEntity testUser1 = new UserEntity(null, "test1", "user1", new ArrayList<>());
    UserEntity savedUser = userRepository.save(testUser1);

    savedUser.setName("test2");
    savedUser.setSurname("user2");
    UserEntity updatedUser = userRepository.save(savedUser);
    Optional<UserEntity> foundUser = userRepository.findById(updatedUser.getId());

    assertTrue(foundUser.isPresent());
    assertEquals("test2", foundUser.get().getName());
    assertEquals("user2", foundUser.get().getSurname());
    assertEquals(testUser1.getId(), foundUser.get().getId());
  }

  @Test
  @DisplayName("Тест на валидацию данных пользователя")
  public void testValidation() {
    UserEntity testUser = new UserEntity(null, "", "user1", new ArrayList<>());

    assertThrows(ConstraintViolationException.class, () -> {
      userRepository.save(testUser);
    });
  }
}
