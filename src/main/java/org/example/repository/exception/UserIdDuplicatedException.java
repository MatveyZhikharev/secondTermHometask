package org.example.repository.exception;

public class UserIdDuplicatedException extends RuntimeException {
  public UserIdDuplicatedException(String message) {
    super(message);
  }
  public UserIdDuplicatedException(String message, Throwable cause) {
    super(message, cause);
  }
}
