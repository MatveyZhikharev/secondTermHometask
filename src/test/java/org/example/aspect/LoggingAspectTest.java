package org.example.aspect;

import org.example.controller.UserController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@EnableAspectJAutoProxy
class LoggingAspectTest {
  @Autowired
  private UserController userController;

  @Autowired
  private LoggingAspect loggingAspect;

  @Test
  public void testLoggingAspect() {
    for (int i = 1; i < 5; i++) {
      userController.getAllUsers(0L);
      assertEquals(i * 2, loggingAspect.aspectNumber);
    }
  }
}