package org.example.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Book;
import org.example.entity.BookId;
import org.example.entity.User;
import org.example.entity.UserId;
import org.example.repository.BookRepository;
import org.example.repository.UserRepository;
import org.example.repository.exception.BookNotFoundException;
import org.example.repository.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class BookService {
  private final BookRepository bookRepository;
  private final UserRepository userRepository;

  public List<Book> getAll() {
    log.info("Получение всех книг");
    return bookRepository.findAll();
  }

  public Book getById(BookId bookId) {
    log.info("Получение книги с ID: {}", bookId.toString());
    return bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException(bookId.toString()));
  }

  public BookId create(String title, UserId authorId) {
    log.info("Создание книги: {}", title + " " + authorId);
    User user = userRepository.findById(authorId).orElseThrow(() -> new BookNotFoundException(authorId.toString()));
    BookId bookId = bookRepository.create(title, authorId);
    user.getBooks().add(bookId);
    return bookId;
  }

  public Book update(BookId bookId, Book updatedBook) {
    log.info("Полное обновление книги: {}", updatedBook);
    bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException(bookId.toString()));
    User user = userRepository.findById(updatedBook.getAuthorId()).orElseThrow(() -> new BookNotFoundException(updatedBook.getAuthorId().toString()));
    if (!user.getBooks().contains(bookId)) {
      user.getBooks().add(bookId);
    }
    return bookRepository.update(bookId, updatedBook);
  }

  public Book patch(BookId bookId, Book updatedBook) {
    log.info("Частичное обновление книги: {}", updatedBook);
    Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException(bookId.toString()));
    if (!updatedBook.getTitle().isEmpty()) {
      book.setTitle(updatedBook.getTitle());
    }
    User user = userRepository.findById(updatedBook.getAuthorId()).orElseThrow(() -> new BookNotFoundException(updatedBook.getAuthorId().toString()));
    if (!user.getBooks().contains(bookId)) {
      user.getBooks().add(bookId);
    }
    return bookRepository.update(bookId, updatedBook);
  }

  public void delete(BookId bookId) {
    log.info("Удаление книги с ID: {}", bookId);
    Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException(bookId.toString()));
    User user = userRepository.findById(book.getAuthorId()).orElseThrow(() -> new UserNotFoundException(bookId.toString()));
    user.getBooks().remove(bookId);
    bookRepository.delete(bookId);
  }
}
