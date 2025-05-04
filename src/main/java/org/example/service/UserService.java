package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.Dto.UserDto;
import org.example.entity.UserEntity;
import org.example.repository.BookRepository;
import org.example.repository.UserRepository;
import org.example.repository.exception.BookNotFoundException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class UserService {
  private final UserRepository userRepository;
  private final BookRepository bookRepository;
  private Set<String> createdUserFullNames = ConcurrentHashMap.newKeySet();

  public UserService(UserRepository userRepository, BookRepository bookRepository) {
    this.userRepository = userRepository;
    this.bookRepository = bookRepository;
  }

  @Async
  @Transactional(readOnly = true)
  public CompletableFuture<List<UserDto>> getAll() {
    log.info("Получение всех пользователей");
    ArrayList<UserDto> userDtos = new ArrayList<>();
    for (UserEntity userEntity : userRepository.findAll()) {
      userDtos.add(new UserDto(userEntity));
    }
    return CompletableFuture.completedFuture(userDtos);
  }

  // At Least Once
  @Retryable(value = RuntimeException.class, maxAttempts = 5, backoff = @Backoff(delay = 10000))
  @Transactional(readOnly = true)
  public UserDto getById(Long userId) {
    log.info("Получение пользователя с ID: {}", userId.toString());
    return new UserDto(userRepository.findById(userId).orElseThrow(() -> new BookNotFoundException(userId.toString())));
  }

  // Exactly Once
  @Transactional
  public Long create(String name, String surname, List<Long> books) {
    log.info("Создание пользователя: {}", name + " " + surname);
    UserEntity user = new UserEntity();
    user.setName(name);
    user.setSurname(surname);
    user.setBooks(new ArrayList<>());
    for (long i : books) {
      user.getBooks().add(bookRepository.findById(i).orElseThrow(() -> new BookNotFoundException(i + "")));
    }
    if (!createdUserFullNames.add(name + surname)) {
      return null;
    }
    return userRepository.save(user).getId();
  }

  @Transactional
  public UserDto update(Long userId, UserEntity updatedUser) {
    log.info("Полное обновление пользователя: {}", updatedUser);
    userRepository.findById(userId).orElseThrow(() -> new BookNotFoundException(userId.toString()));
    return new UserDto(userRepository.save(updatedUser));
  }

  @Transactional
  public UserDto patch(Long userId, UserEntity updatedUser) {
    log.info("Частичное обновление пользователя: {}", updatedUser);
    UserEntity user = userRepository.findById(userId).orElseThrow(() -> new BookNotFoundException(userId.toString()));
    if (!updatedUser.getName().isEmpty()) {
      user.setName(updatedUser.getName());
    }
    if (!updatedUser.getSurname().isEmpty()) {
      user.setSurname(updatedUser.getSurname());
    }
    if (!updatedUser.getBooks().isEmpty()) {
      user.setBooks(updatedUser.getBooks());
    }
    return new UserDto(userRepository.save(updatedUser));
  }

  @Transactional
  public void delete(Long userId) {
    log.info("Удаление пользователя с ID: {}", userId);
    userRepository.delete(userRepository.getById(userId));
  }
}
