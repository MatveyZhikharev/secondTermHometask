package org.example.repository.exception;

public class CourseNotFoundException extends RuntimeException {
  public CourseNotFoundException(String message) {
    super(message);
  }

  public CourseNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
