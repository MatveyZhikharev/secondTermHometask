package org.example.entity;

import java.util.Objects;

public class University {
  private final long id;
  private final String name;
  private final String address;

  public University(long id, String name, String address) {
    this.id = id;
    this.name = name;
    this.address = address;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    University that = (University) o;
    return id == that.id;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
