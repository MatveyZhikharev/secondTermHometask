package org.example.entity;

import java.util.Objects;

public class Course {
  private final int id;
  private final String name;
  private final String description;

  public Course(int id, String name, String description) {
    this.id = id;
    this.name = name;
    this.description = description;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Course course = (Course) o;
    return id == course.id;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
