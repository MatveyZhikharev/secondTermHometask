package org.example.repository;

import org.example.entity.Book;

import java.util.List;

public interface BookRepository {
  long generateId();

  List<Book> findAll();

  Book findById(long bookId);

  void create(Book book);

  void update(Book book);

  void delete(long bookId);
}
