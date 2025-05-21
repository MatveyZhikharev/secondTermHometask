package org.example.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.Dto.MessageDto;
import org.example.Dto.UserDto;
import org.example.entity.UserEntity;
import org.example.enums.Action;
import org.example.repository.BookRepository;
import org.example.repository.UserRepository;
import org.example.repository.exception.BookNotFoundException;
import org.example.repository.exception.UserNotFoundException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
@Service
@Slf4j
public class UserService {
  private final UserRepository userRepository;
  private final BookRepository bookRepository;
  private final KafkaProducerService kafkaProducerService;
  private Set<String> createdUserFullNames = ConcurrentHashMap.newKeySet();

  @Async
  @Transactional(readOnly = true)
  public CompletableFuture<List<UserDto>> getAll(String userId) {
    log.info("Получение всех пользователей");
    ArrayList<UserDto> userDtos = new ArrayList<>();
    for (UserEntity userEntity : userRepository.findAll()) {
      userDtos.add(new UserDto(userEntity));
    }
    kafkaProducerService.sendMessage(
        MessageDto.builder()
            .userId(UUID.fromString(userId))
            .type(Action.SELECT.toString())
            .log("User with ID: " + userId + " got all users")
            .build()
    );
    return CompletableFuture.completedFuture(userDtos);
  }

  // At Least Once
  @Retryable(value = RuntimeException.class, maxAttempts = 5, backoff = @Backoff(delay = 10000))
  @Transactional(readOnly = true)
  public UserDto getById(String requesterId, Long userId) {
    log.info("Получение пользователя с ID: {}", userId.toString());
    UserEntity user = userRepository.findById(userId).orElseThrow(() -> new BookNotFoundException(userId.toString()));
    kafkaProducerService.sendMessage(
        MessageDto.builder()
            .userId(UUID.fromString(requesterId))
            .type(Action.SELECT.toString())
            .log("User with ID: " + requesterId + " got user" + user)
            .build()
    );
    return new UserDto(user);
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
    kafkaProducerService.sendMessage(
        MessageDto.builder()
            .userId(UUID.fromString(user.getId().toString()))
            .type(Action.SELECT.toString())
            .log("User with ID: " + user.getId() + " updated user: " + user.getId())
            .build()
    );
    return userRepository.save(user).getId();
  }

  @Transactional
  public UserDto update(String requesterId, Long userId, UserEntity updatedUser) {
    log.info("Полное обновление пользователя: {}", updatedUser);
    userRepository.findById(userId).orElseThrow(() -> new BookNotFoundException(userId.toString()));
    updatedUser.setId(userId);
    kafkaProducerService.sendMessage(
        MessageDto.builder()
            .userId(UUID.fromString(requesterId))
            .type(Action.SELECT.toString())
            .log("User with ID: " + requesterId + " updated user: " + updatedUser)
            .build()
    );
    return new UserDto(userRepository.save(updatedUser));
  }

  @Transactional
  public UserDto patch(String requesterId, Long userId, UserEntity updatedUser) {
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
    kafkaProducerService.sendMessage(
        MessageDto.builder()
            .userId(UUID.fromString(requesterId))
            .type(Action.SELECT.toString())
            .log("User with ID: " + requesterId + " patched user: " + updatedUser)
            .build()
    );
    return new UserDto(userRepository.save(updatedUser));
  }

  @Transactional
  public void delete(String requesterId, Long userId) {
    log.info("Удаление пользователя с ID: {}", userId);
    UserEntity user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId.toString()));
    kafkaProducerService.sendMessage(
        MessageDto.builder()
            .userId(UUID.fromString(requesterId))
            .type(Action.SELECT.toString())
            .log("User with ID: " + requesterId + " deleted user: " + user)
            .build()
    );
    userRepository.delete(user);
  }
}
