package org.example.repository;

import org.example.entity.Book;
import org.example.entity.BookId;
import org.example.entity.UserId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

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

  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private WebClient webClient;

  @Override
  public List<Book> findAll() {
    String randomUrl = "https://ya.ru/";
    String response = restTemplate.getForObject(randomUrl, String.class);
    System.out.println("Ответ RestTemplate: " + response);
    return new ArrayList<>(books.values());
  }

  @Override
  public Optional<Book> findById(BookId bookId) {
    String randomUrl = "https://ya.ru/";
    String response = webClient.get()
        .uri(randomUrl)
        .retrieve()
        .bodyToMono(String.class)
        .block();
    System.out.println("Ответ WebClient: " + response);
    return Optional.ofNullable(books.get(bookId));
  }

  @Override
  public void delete(BookId bookId) {
    books.remove(bookId);
  }

  @Override
  public BookId create(Book book) {
    BookId id = new BookId(idCounter.getAndIncrement());
    book.setId(id);
    books.put(id, book);
    return id;
  }

  @Override
  public Book update(BookId bookId, Book updatedBook) {
    books.put(bookId, updatedBook);
    return updatedBook;
  }
}
