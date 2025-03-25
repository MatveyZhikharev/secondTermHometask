package org.example.controller;

import io.github.resilience4j.ratelimiter.RateLimiter;
import org.example.entity.Book;
import org.example.entity.BookId;
import org.example.request.BookCreateRequest;
import org.example.request.BookPatchRequest;
import org.example.request.BookPutRequest;
import org.example.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*
rateLimiter
 */
@RestController
public class BookControllerImpl implements BookController {
  private final BookService bookService;
  private final RateLimiter rateLimiter = RateLimiter.ofDefaults("apiRateLimiter");

  public BookControllerImpl(BookService bookService) {
    this.bookService = bookService;
  }

  @Override
  public ResponseEntity<List<Book>> getAllBooks() {
    return rateLimiter.executeSupplier(() -> ResponseEntity.ok(bookService.getAll()));
  }

  @Override
  public ResponseEntity<Book> getBookById(BookId bookId) {
    return rateLimiter.executeSupplier(() -> ResponseEntity.ok(bookService.getById(bookId)));
  }

  @Override
  public ResponseEntity<BookId> createBook(BookCreateRequest bookDraft) {
    return rateLimiter.executeSupplier(
        () -> ResponseEntity.status(HttpStatus.CREATED).body(bookService.create(
            new Book(bookDraft.getId(), bookDraft.getTitle(), bookDraft.getAuthorId())
        ))
    );
  }

  @Override
  public ResponseEntity<Book> patchBook(BookId bookId, BookPatchRequest book) {
    Book castedBook = new Book(book.getId(), book.getTitle(), book.getUserId());
    return rateLimiter.executeSupplier(() -> ResponseEntity.ok(bookService.update(bookId, castedBook)));
  }

  @Override
  public ResponseEntity<Book> updateBook(BookId bookId, BookPutRequest book) {
    Book castedBook = new Book(book.getId(), book.getTitle(), book.getAuthorId());
    return rateLimiter.executeSupplier(() -> ResponseEntity.ok(bookService.patch(bookId, castedBook)));
  }

  @Override
  public ResponseEntity<Void> deleteBook(BookId bookId) {
    bookService.delete(bookId);
    return rateLimiter.executeSupplier(() -> ResponseEntity.noContent().build());
  }
}
