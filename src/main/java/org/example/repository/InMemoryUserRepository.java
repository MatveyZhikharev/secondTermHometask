package org.example.repository;

import org.example.entity.BookId;
import org.example.entity.User;
import org.example.entity.UserId;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InMemoryUserRepository implements UserRepository {
  private final Map<UserId, User> users = new ConcurrentHashMap<>();
  private final AtomicInteger idCounter = new AtomicInteger(1);

  @Override
  public List<User> findAll() {
    return new ArrayList<>(users.values());
  };

  @Override
  public Optional<User> findById(UserId userId) {
    return Optional.ofNullable(users.get(userId));
  };

  @Override
  public void delete(UserId id) {
    users.remove(id);
  };

  @Override
  public UserId create(String name, String surname, List<BookId> books) {
    UserId id = new UserId(idCounter.getAndIncrement());
    User user = new User(id, name, surname, books);
    users.put(id, user);
    return id;
  }

  @Override
  public User update(UserId id, User updatedUser) {
    users.put(id, updatedUser);
    return updatedUser;
  }
}
