package org.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.example.entity.BookId;
import org.example.entity.User;
import org.example.entity.Book;
import org.example.entity.UserId;
import org.example.request.BookPatchRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class EndToEndTest {
  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  @DisplayName("Тест всей логики приложения")
  public void E2ETest() {
    User user1 = new User(new UserId(1), "User1", "User1ov", new ArrayList<>());
    User user2 = new User(new UserId(2), "User2", "User2ov", new ArrayList<>());
    ResponseEntity<UserId> createUserResponse1 =
        restTemplate.postForEntity("http://localhost:" + port + "/api/users/", user1, UserId.class);
    ResponseEntity<UserId> createUserResponse2 =
        restTemplate.postForEntity("http://localhost:" + port + "/api/users/", user2, UserId.class);
    assertEquals(HttpStatus.CREATED, createUserResponse1.getStatusCode());
    assertEquals(HttpStatus.CREATED, createUserResponse2.getStatusCode());
    assertEquals(new UserId(1), createUserResponse1.getBody());
    assertEquals(new UserId(2), createUserResponse2.getBody());

    ResponseEntity<User> getUserResponse =
        restTemplate.getForEntity("http://localhost:" + port + "/api/users/1", User.class);
    assertEquals(HttpStatus.OK, getUserResponse.getStatusCode());
    assertEquals(user1, getUserResponse.getBody());

    Book book = new Book(new BookId(1), "BookOfUser1", new UserId(1));
    ResponseEntity<BookId> createBookResponse =
        restTemplate.postForEntity("http://localhost:" + port + "/api/books/", book, BookId.class);
    assertEquals(HttpStatus.CREATED, createBookResponse.getStatusCode());
    assertEquals(new BookId(1), createBookResponse.getBody());

    ResponseEntity<User> getUser1DataResponse =
        restTemplate.getForEntity("http://localhost:" + port + "/api/users/1", User.class);
    assertEquals(HttpStatus.OK, getUser1DataResponse.getStatusCode());
    assertEquals(book.getId(), getUser1DataResponse.getBody().getBooks().get(0));

    BookPatchRequest newBook = new BookPatchRequest(new BookId(1), "BookOfUser2", new UserId(2));
    Book updatedBook = new Book(new BookId(1), "BookOfUser2", new UserId(2));
    Book patchBookResponse =
        restTemplate.patchForObject("http://localhost:" + port + "/api/books/1", newBook, Book.class);
    assertEquals(patchBookResponse, updatedBook);
    ResponseEntity<User> getUser2DataResponse =
        restTemplate.getForEntity("http://localhost:" + port + "/api/users/2", User.class);
    assertEquals(HttpStatus.OK, getUser2DataResponse.getStatusCode());
    assertEquals(updatedBook.getId(), getUser2DataResponse.getBody().getBooks().get(0));

    restTemplate.delete("http://localhost:" + port + "/api/books/1", updatedBook);
    getUser2DataResponse =
        restTemplate.getForEntity("http://localhost:" + port + "/api/users/2", User.class);
    assertEquals(new ArrayList<>(), getUser2DataResponse.getBody().getBooks());
  }
}