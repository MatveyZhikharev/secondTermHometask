package org.example.service;

import org.example.repository.CourseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CourseService {
  private static final Logger log = LoggerFactory.getLogger(CourseService.class);

  private final CourseRepository courseRepository;

  public CourseService(CourseRepository courseRepository) {
    this.courseRepository = courseRepository;
  }

  public void logCall() {
    log.info("Вызван метод CourseService");
  }
}
