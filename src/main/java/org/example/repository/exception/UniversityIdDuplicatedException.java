package org.example.repository.exception;

public class UniversityIdDuplicatedException extends RuntimeException {
  public UniversityIdDuplicatedException(String message) {
    super(message);
  }
  public UniversityIdDuplicatedException(String message, Throwable cause) {
    super(message, cause);
  }
}
