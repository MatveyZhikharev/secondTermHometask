package org.example.controller;

import io.github.resilience4j.ratelimiter.RateLimiter;
import org.example.Dto.BookDto;
import org.example.entity.BookEntity;
import org.example.entity.UserEntity;
import org.example.repository.UserRepository;
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
  private final UserRepository userRepository;

  public BookControllerImpl(BookService bookService, UserRepository userRepository) {
    this.bookService = bookService;
    this.userRepository = userRepository;
  }

  @Override
  public ResponseEntity<List<BookDto>> getAllBooks(String requesterId) {
    return rateLimiter.executeSupplier(() -> ResponseEntity.ok(bookService.getAll()));
  }

  @Override
  public ResponseEntity<BookDto> getBookById(String requesterId, Long bookId) {
    return rateLimiter.executeSupplier(() -> ResponseEntity.ok(bookService.getById(bookId)));
  }

  @Override
  public ResponseEntity<Long> createBook(String requesterId, BookCreateRequest bookDraft) {
    return rateLimiter.executeSupplier(
        () -> ResponseEntity.status(HttpStatus.CREATED).body(bookService.create(bookDraft.getTitle(), bookDraft.getAuthorId()))
    );
  }

  @Override
  public ResponseEntity<BookDto> patchBook(String requesterId, Long bookId, BookPatchRequest book) {
    BookEntity castedBook = new BookEntity(book.getId(), book.getTitle(), userRepository.findById(bookId).get());
    return rateLimiter.executeSupplier(() -> ResponseEntity.ok(bookService.update(bookId, castedBook)));
  }

  @Override
  public ResponseEntity<BookDto> updateBook(String requesterId, Long bookId, BookPutRequest book) {
    BookEntity castedBook = new BookEntity(book.getId(), book.getTitle(), userRepository.findById(book.getAuthorId()).get());
    return rateLimiter.executeSupplier(() -> ResponseEntity.ok(bookService.patch(bookId, castedBook)));
  }

  @Override
  public ResponseEntity<Void> deleteBook(String requesterId, Long bookId) {
    bookService.delete(bookId);
    return rateLimiter.executeSupplier(() -> ResponseEntity.noContent().build());
  }
}
