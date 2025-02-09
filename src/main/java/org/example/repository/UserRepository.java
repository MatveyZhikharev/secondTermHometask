package org.example.repository;

import org.example.entity.User;

import java.util.List;

public interface UserRepository {
  long generateId();

  List<User> findAll();

  User findById(long userId);

  void create(User user);

  void update(User user);

  void delete(long userId);
}
