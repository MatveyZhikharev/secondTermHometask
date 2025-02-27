package org.example.repository;

import org.example.entity.Book;
import org.example.entity.BookId;
import org.example.entity.UserId;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class InMemoryBookRepository implements BookRepository {
  private final Map<BookId, Book> books = new ConcurrentHashMap<>();
  private final AtomicInteger idCounter = new AtomicInteger(1);

  @Override
  public List<Book> findAll() {
    return new ArrayList<>(books.values());
  }

  @Override
  public Optional<Book> findById(BookId bookId) {
    return Optional.ofNullable(books.get(bookId));
  }

  @Override
  public void delete(BookId bookId) {
    books.remove(bookId);
  }

  @Override
  public BookId create(String title, UserId authorId) {
    BookId id = new BookId(idCounter.getAndIncrement());
    Book book = new Book(id, title, authorId);
    books.put(id, book);
    return id;
  }

  @Override
  public Book update(BookId bookId, Book updatedBook) {
    books.put(bookId, updatedBook);
    return updatedBook;
  }
}
