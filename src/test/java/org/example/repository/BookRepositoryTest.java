package org.example.repository;

import jakarta.validation.ConstraintViolationException;
import org.example.entity.BookEntity;
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
class BookRepositoryTest {
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
  private BookRepository bookRepository;

  @Autowired
  private UserRepository userRepository;

  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
  }

  @Test
  @DisplayName("Тест на сохранение и поиск книги по ID")
  public void testSaveAndFindById() {
    UserEntity testUser = new UserEntity(null, "test", "user", new ArrayList<>());
    userRepository.save(testUser);
    BookEntity testBook = new BookEntity(null, "test book", testUser);
    BookEntity savedBook = bookRepository.save(testBook);

    Optional<BookEntity> foundBook = bookRepository.findById(savedBook.getId());
    assertTrue(foundBook.isPresent());

    assertEquals(savedBook.getId(), foundBook.get().getId());
    assertEquals("test book", foundBook.get().getTitle());
    assertEquals(testUser.getId(), foundBook.get().getAuthor().getId());
  }

  @Test
  @DisplayName("Тест на поиск несуществующей книги")
  public void testFindByIdAndNotFound() {
    Optional<BookEntity> foundBook = bookRepository.findById(999L);
    assertFalse(foundBook.isPresent());
  }

  @Test
  @DisplayName("Тест на удаление книги")
  public void testDeleteBook() {
    UserEntity testUser = new UserEntity(null, "test", "user", new ArrayList<>());
    userRepository.save(testUser);
    BookEntity userBook = new BookEntity(null, "test book", testUser);
    BookEntity savedBook = bookRepository.save(userBook);

    bookRepository.delete(savedBook);
    Optional<BookEntity> foundBook = bookRepository.findById(savedBook.getId());
    assertFalse(foundBook.isPresent());
  }

  @Test
  @DisplayName("Тест на получение всех книг")
  public void testFindAll() {
    UserEntity testUser1 = new UserEntity(null, "test", "user1", new ArrayList<>());
    UserEntity testUser2 = new UserEntity(null, "test", "user2", new ArrayList<>());
    userRepository.save(testUser1);
    userRepository.save(testUser2);

    BookEntity testBook1 = new BookEntity(null, "test book 1", testUser1);
    BookEntity testBook2 = new BookEntity(null, "test book 2", testUser2);
    bookRepository.save(testBook1);
    bookRepository.save(testBook2);

    List<BookEntity> books = bookRepository.findAll();
    assertEquals(2, books.size());
    assertTrue(books.stream().anyMatch(b -> b.getTitle().equals("test book 1")));
    assertTrue(books.stream().anyMatch(b -> b.getTitle().equals("test book 2")));
  }

  @Test
  @DisplayName("Тест на обновление книги")
  public void testUpdateBook() {
    UserEntity testUser1 = new UserEntity(null, "test", "user1", new ArrayList<>());
    UserEntity testUser2 = new UserEntity(null, "test", "user2", new ArrayList<>());
    userRepository.save(testUser1);
    userRepository.save(testUser2);

    BookEntity testBook = new BookEntity(null, "test book", testUser1);
    BookEntity savedBook = bookRepository.save(testBook);

    savedBook.setTitle("test book edited");
    savedBook.setAuthor(testUser2);
    BookEntity updatedBook = bookRepository.save(savedBook);
    Optional<BookEntity> foundBook = bookRepository.findById(updatedBook.getId());

    assertTrue(foundBook.isPresent());
    assertEquals("test book edited", foundBook.get().getTitle());
    assertEquals(testUser2.getId(), foundBook.get().getAuthor().getId());
  }

  @Test
  @DisplayName("Тест на валидацию данных книги")
  public void testValidation() {
    UserEntity testUser = new UserEntity(null, "test", "user1", new ArrayList<>());
    userRepository.save(testUser);
    BookEntity userBook = new BookEntity(null, "", testUser);
    assertThrows(ConstraintViolationException.class, () -> {
      bookRepository.save(userBook);
    });
  }
}
