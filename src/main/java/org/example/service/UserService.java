package org.example.service;

import io.github.resilience4j.ratelimiter.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.BookId;
import org.example.entity.User;
import org.example.entity.UserId;
import org.example.repository.UserRepository;
import org.example.repository.exception.BookNotFoundException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class UserService {
  private final UserRepository userRepository;
  private Set<String> createdUserFullNames = ConcurrentHashMap.newKeySet();

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Async
  public CompletableFuture<List<User>> getAll() {
    log.info("Получение всех пользователей");
    return CompletableFuture.completedFuture(userRepository.findAll());
  }

  // At Least Once
  @Retryable(value = RuntimeException.class, maxAttempts = 5, backoff = @Backoff(delay = 10000))
  public User getById(UserId userId) {
    log.info("Получение пользователя с ID: {}", userId.toString());
    return userRepository.findById(userId).orElseThrow(() -> new BookNotFoundException(userId.toString()));
  }

  // Exactly Once
  public UserId create(String name, String surname, List<BookId> books) {
    log.info("Создание пользователя: {}", name + " " + surname);
    if (!createdUserFullNames.add(name + surname)) {
      return null;
    }
    return userRepository.create(name, surname, books);
  }

  public User update(UserId userId, User updatedUser) {
    log.info("Полное обновление пользователя: {}", updatedUser);
    userRepository.findById(userId).orElseThrow(() -> new BookNotFoundException(userId.toString()));
    return userRepository.update(userId, updatedUser);
  }

  public User patch(UserId userId, User updatedUser) {
    log.info("Частичное обновление пользователя: {}", updatedUser);
    User user = userRepository.findById(userId).orElseThrow(() -> new BookNotFoundException(userId.toString()));
    if (!updatedUser.getName().isEmpty()) {
      user.setName(updatedUser.getName());
    }
    if (!updatedUser.getSurname().isEmpty()) {
      user.setSurname(updatedUser.getSurname());
    }
    if (!updatedUser.getBooks().isEmpty()) {
      user.setBooks(updatedUser.getBooks());
    }
    return userRepository.update(userId, updatedUser);
  }

  public void delete(UserId userId) {
    log.info("Удаление пользователя с ID: {}", userId);
    userRepository.delete(userId);
  }
}
