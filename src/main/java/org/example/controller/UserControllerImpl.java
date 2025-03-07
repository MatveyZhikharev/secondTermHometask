package org.example.controller;

import org.example.entity.User;
import org.example.entity.UserId;
import org.example.request.UserCreateRequest;
import org.example.request.UserPatchRequest;
import org.example.request.UserPutRequest;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserControllerImpl implements UserController {
  private final UserService userService;

  public UserControllerImpl(UserService userService) {
    this.userService = userService;
  }

  @Override
  public ResponseEntity<List<User>> getAllUsers() {
    return ResponseEntity.ok(userService.getAll());
  }

  @Override
  public ResponseEntity<User> getUserById(UserId id) {
    return ResponseEntity.ok(userService.getById(id));
  }

  @Override
  public ResponseEntity<UserId> createUser(UserCreateRequest userDraft) {
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(userDraft.getName(), userDraft.getSurname(), userDraft.getBooks()));
  }

  @Override
  public ResponseEntity<User> patchUser(UserId userId, UserPatchRequest user) {
    User castedUser = new User(user.getId(), user.getName(), user.getSurname(), user.getBooks());
    return ResponseEntity.ok(userService.patch(userId, castedUser));
  }

  @Override
  public ResponseEntity<User> updateUser(UserId userId, UserPutRequest user) {
    User castedUser = new User(user.getId(), user.getName(), user.getSurname(), user.getBooks());
    return ResponseEntity.ok(userService.update(userId, castedUser));
  }

  @Override
  public ResponseEntity<Void> deleteUser(UserId userId) {
    userService.delete(userId);
    return ResponseEntity.noContent().build();
  }
}
