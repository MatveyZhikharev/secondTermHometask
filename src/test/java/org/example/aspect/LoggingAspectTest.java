package org.example.aspect;

import org.example.controller.UserController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class LoggingAspectTest {
  @Autowired
  private UserController userController;

  @Autowired
  private LoggingAspect loggingAspect;

  @Test
  public void testLoggingAspect() {
    for (int i = 1; i < 5; i++) {
      userController.getAllUsers();
      assertEquals(i * 2, loggingAspect.aspectNumber);
    }
  }
}