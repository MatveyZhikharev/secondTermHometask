package org.example.controller;

import org.example.entity.Book;
import org.example.entity.BookId;
import org.example.request.BookCreateRequest;
import org.example.request.BookPatchRequest;
import org.example.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BookControllerImpl implements BookController {
  private final BookService bookService;

  public BookControllerImpl(BookService bookService) {
    this.bookService = bookService;
  }

  @Override
  public ResponseEntity<List<Book>> getAllBooks() {
    return ResponseEntity.ok(bookService.getAll());
  }

  @Override
  public ResponseEntity<Book> getBookById(BookId bookId) {
    return ResponseEntity.ok(bookService.getById(bookId));
  }

  @Override
  public ResponseEntity<BookId> createBook(BookCreateRequest bookDraft) {
    return ResponseEntity.status(HttpStatus.CREATED).body(bookService.create(bookDraft.getTitle(), bookDraft.getAuthorId()));
  }

  @Override
  public ResponseEntity<Book> updateBook(BookId bookId, BookPatchRequest book) {
    Book castedBook = new Book(book.getId(), book.getTitle(), book.getUserId());
    return ResponseEntity.ok(bookService.update(bookId, castedBook));
  }

  @Override
  public ResponseEntity<Void> deleteBook(BookId bookId) {
    bookService.delete(bookId);
    return ResponseEntity.noContent().build();
  }
}
