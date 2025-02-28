package org.example.repository;


import org.example.entity.BookId;
import org.example.entity.User;
import org.example.entity.UserId;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
  List<User> findAll();

  Optional<User> findById(UserId id);

  void delete(UserId id);

  UserId create(String name, String surname, List<BookId> books);

  User update(UserId id, User user);
}
