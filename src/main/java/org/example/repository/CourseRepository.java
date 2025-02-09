package org.example.repository;

import org.example.entity.Course;

import java.util.List;

public interface CourseRepository {
  long generateId();

  List<Course> findAll();

  Course findById(long courseId);

  void create(Course course);

  void update(Course course);

  void delete(long courseId);
}

