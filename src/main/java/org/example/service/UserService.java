package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.entity.Book;
import org.example.entity.BookId;
import org.example.entity.User;
import org.example.entity.UserId;
import org.example.repository.UserRepository;
import org.example.repository.exception.BookNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserService {
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getAll() {
    log.info("Получение всех пользователей");
    return userRepository.findAll();
  }

  public User getById(UserId userId) {
    log.info("Получение пользователя с ID: {}", userId.toString());
    return userRepository.findById(userId).orElseThrow(() -> new BookNotFoundException(userId.toString()));
  }

  public UserId create(String name, String surname, List<BookId> books) {
    log.info("Создание пользователя: {}", name + " " + surname);
    return userRepository.create(name, surname, books);
  }

  public User update(UserId userId, User updatedUser) {
    log.info("Полное обновление пользователя: {}", updatedUser);
    userRepository.findById(userId).orElseThrow(() -> new BookNotFoundException(userId.toString()));
    return userRepository.update(userId, updatedUser);
  }

  public User patch(UserId userId, User updatedUser) {
    log.info("Частичное бновление пользователя: {}", updatedUser);
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
