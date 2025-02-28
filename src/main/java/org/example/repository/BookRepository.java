package org.example.repository;


import org.example.entity.Book;
import org.example.entity.BookId;
import org.example.entity.UserId;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
  List<Book> findAll();

  Optional<Book> findById(BookId id);

  void delete(BookId id);

  BookId create(String title, UserId authorId);

  Book update(BookId bookId, Book updatedBook);
}
