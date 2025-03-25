package org.example.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Aspect
@Component
public class LoggingAspect {
  public int aspectNumber;
  @Before("execution(* org.example.service.*.*(..))")
  public void logBefore(JoinPoint joinPoint) {
    System.out.println("Перед вызовом метода: " + joinPoint.getSignature().getName());
  }

  @Around("execution(* org.example.controller.*.*(..))")
  public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    long startTime = Instant.now().getEpochSecond();
    ++aspectNumber;
    Object result = joinPoint.proceed();
    ++aspectNumber;
    long endTime = Instant.now().getEpochSecond();
    System.out.println("Метод " + joinPoint.getSignature().getName()
        + " выполнен за " + (endTime - startTime) + " мс");
    return result;
  }
}
