package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;


@Entity
@Data
@Table(name = "courses")
public class Course {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

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
