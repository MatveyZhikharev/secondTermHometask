package org.example.repository.exception;

public class UniversityNotFoundException extends RuntimeException {
  public UniversityNotFoundException(String message) {
    super(message);
  }
  public UniversityNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
