package org.example.controller;

import io.github.resilience4j.ratelimiter.RateLimiter;
import org.example.entity.User;
import org.example.entity.UserId;
import org.example.request.UserCreateRequest;
import org.example.request.UserPatchRequest;
import org.example.request.UserPutRequest;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.springframework.web.bind.annotation.RestController;

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

  public UserControllerImpl(UserService userService) {
    this.userService = userService;
  }

  @Override
  public CompletableFuture<ResponseEntity<List<User>>> getAllUsers() {
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
  public ResponseEntity<User> getUserById(UserId id) {
    return circuitBreaker.executeSupplier(
        () -> ResponseEntity.ok(userService.getById(id))
    );
  }

  @Override
  public ResponseEntity<UserId> createUser(UserCreateRequest userDraft) {
    return circuitBreaker.executeSupplier(
        () -> ResponseEntity.status(HttpStatus.CREATED).body(userService.create(userDraft.getName(), userDraft.getSurname(), userDraft.getBooks()))
    );
  }

  @Override
  public ResponseEntity<User> patchUser(UserId userId, UserPatchRequest user) {
    User castedUser = new User(user.getId(), user.getName(), user.getSurname(), user.getBooks());
    return circuitBreaker.executeSupplier(
        () -> ResponseEntity.ok(userService.patch(userId, castedUser))
    );
  }

  @Override
  public ResponseEntity<User> updateUser(UserId userId, UserPutRequest user) {
    User castedUser = new User(user.getId(), user.getName(), user.getSurname(), user.getBooks());
    return circuitBreaker.executeSupplier(
        () -> ResponseEntity.ok(userService.update(userId, castedUser))
    );
  }

  @Override
  public ResponseEntity<Void> deleteUser(UserId userId) {
    userService.delete(userId);
    return circuitBreaker.executeSupplier(
        () -> ResponseEntity.noContent().build()
    );
  }
}
