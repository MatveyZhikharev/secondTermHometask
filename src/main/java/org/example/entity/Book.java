package org.example.entity;

import java.util.Objects;

public class Book {
  private final long id;
  private final String title;
  private final String author;
  private final String publisher;
  private final String releaseDate;

  public Book(long id, String title, String author, String publisher, String releaseDate) {
    this.id = id;
    this.title = title;
    this.author = author;
    this.publisher = publisher;
    this.releaseDate = releaseDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Book book = (Book) o;
    return id == book.id;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
