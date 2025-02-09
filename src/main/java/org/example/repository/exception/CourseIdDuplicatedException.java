package org.example.repository.exception;

public class CourseIdDuplicatedException extends RuntimeException {
  public CourseIdDuplicatedException(String message) {
    super(message);
  }
  public CourseIdDuplicatedException(String message, Throwable cause) {
    super(message, cause);
  }
}
