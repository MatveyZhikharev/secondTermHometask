package org.example.controller;

import io.github.resilience4j.ratelimiter.RateLimiter;
import org.example.Dto.UserDto;
import org.example.entity.BookEntity;
import org.example.entity.UserEntity;
import org.example.repository.BookRepository;
import org.example.request.UserCreateRequest;
import org.example.request.UserPatchRequest;
import org.example.request.UserPutRequest;
import org.example.service.BookService;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/*
circuitBreaker (+ rateLimiter in some endpoints)
 */
@RestController
public class UserControllerImpl implements UserController {
  private final UserService userService;
  private final CircuitBreaker circuitBreaker = CircuitBreaker.ofDefaults("apiCircuitBreaker");
  private final RateLimiter rateLimiter = RateLimiter.ofDefaults("apiRateLimiter");
  private final BookRepository bookRepository;

  public UserControllerImpl(UserService userService, BookRepository bookRepository) {
    this.userService = userService;
    this.bookRepository = bookRepository;
  }

  @Override
  public CompletableFuture<ResponseEntity<List<UserDto>>> getAllUsers() {
    return circuitBreaker.executeSupplier(() ->
        rateLimiter.executeSupplier(() ->
            userService.getAll().thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                })
        )
    );
  }

  @Override
  public ResponseEntity<UserDto> getUserById(Long id) {
    return circuitBreaker.executeSupplier(
        () -> ResponseEntity.ok(userService.getById(id))
    );
  }

  @Override
  public ResponseEntity<Long> createUser(UserCreateRequest userDraft) {
    ArrayList<BookEntity> books = new ArrayList<>();
    for (Long book : userDraft.getBooks()) {
      books.add(bookRepository.getById(book));
    }
    return circuitBreaker.executeSupplier(
        () -> ResponseEntity.status(HttpStatus.CREATED).body(userService.create(userDraft.getName(), userDraft.getSurname(), userDraft.getBooks()))
    );
  }

  @Override
  public ResponseEntity<UserDto> patchUser(Long userId, UserPatchRequest user) {
    ArrayList<BookEntity> books = new ArrayList<>();
    for (Long book : user.getBooks()) {
      books.add(bookRepository.getById(book));
    }
    UserEntity castedUser = new UserEntity(user.getId(), user.getName(), user.getSurname(), books);
    return circuitBreaker.executeSupplier(
        () -> ResponseEntity.ok(userService.patch(userId, castedUser))
    );
  }

  @Override
  public ResponseEntity<UserDto> updateUser(Long userId, UserPutRequest user) {
    ArrayList<BookEntity> books = new ArrayList<>();
    for (Long book : user.getBooks()) {
      books.add(bookRepository.getById(book));
    }
    UserEntity castedUser = new UserEntity(user.getId(), user.getName(), user.getSurname(), books);
    return circuitBreaker.executeSupplier(
        () -> ResponseEntity.ok(userService.update(userId, castedUser))
    );
  }

  @Override
  public ResponseEntity<Void> deleteUser(Long userId) {
    userService.delete(userId);
    return circuitBreaker.executeSupplier(
        () -> ResponseEntity.noContent().build()
    );
  }
}
