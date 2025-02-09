package org.example.entity;

import java.util.ArrayList;
import java.util.Objects;

public class User {
  private final long id;
  private final String name;
  private final University university;
  private final ArrayList<Book> books;
  private final ArrayList<Course> courses;

  public User(long id, String name, University university, ArrayList<Book> books, ArrayList<Course> courses) {
    this.id = id;
    this.name = name;
    this.university = university;
    this.books = books;
    this.courses = courses;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return id == user.id;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
