package org.example.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.Dto.BookDto;
import org.example.entity.BookEntity;
import org.example.entity.UserEntity;
import org.example.repository.BookRepository;
import org.example.repository.UserRepository;
import org.example.repository.exception.BookNotFoundException;
import org.example.repository.exception.UserNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class BookService {
  private final BookRepository bookRepository;
  private final UserRepository userRepository;

  @Cacheable(value = "books")
  @Transactional(readOnly = true)
  public List<BookDto> getAll() {
    log.info("Получение всех книг");
    ArrayList<BookDto> bookDtos = new ArrayList<>();
    for (BookEntity book : bookRepository.findAll()) {
      bookDtos.add(new BookDto(book));
    }
    return bookDtos;
  }

  @Cacheable(value = "book", key = "#bookId.hashCode()")
  @Transactional(readOnly = true)
  public BookDto getById(Long bookId) {
    log.info("Получение книги с ID: {}", bookId.toString());
    return new BookDto(bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException(bookId.toString())));
  }

  @CacheEvict(value = "books", allEntries = true)
  @Transactional
  public Long create(String title, Long authorId) {
    log.info("Создание книги: {}", title + " " + authorId);
    UserEntity user = userRepository.findById(authorId).orElseThrow(() -> new BookNotFoundException(authorId.toString()));
    BookEntity book = new BookEntity();
    book.setTitle(title);
    book.setAuthor(user);

    Long bookId = bookRepository.save(book).getId();
    user.getBooks().add(book);
    userRepository.save(user);
    return bookId;
  }

  @CachePut(value = "book", key = "#bookId.hashCode()")
  @Transactional
  public BookDto update(Long bookId, BookEntity updatedBook) {
    log.info("Полное обновление книги: {}", updatedBook);
    bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException(bookId.toString()));
    UserEntity user = userRepository.findById(
        updatedBook.getAuthor().getId()).orElseThrow(() -> new BookNotFoundException(updatedBook.getAuthor().getId().toString()));
    if (!user.getBooks().contains(bookId)) {
      user.getBooks().add(updatedBook);
    }
    userRepository.save(user);
    return new BookDto(bookRepository.save(updatedBook));
  }

  @CachePut(value = "book", key = "#bookId.hashCode()")
  @Transactional
  public BookDto patch(Long bookId, BookEntity updatedBook) {
    log.info("Частичное обновление книги: {}", updatedBook);
    BookEntity book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException(bookId.toString()));
    if (!updatedBook.getTitle().isEmpty()) {
      book.setTitle(updatedBook.getTitle());
    }
    UserEntity user = userRepository.findById(
        updatedBook.getAuthor().getId()).orElseThrow(() -> new BookNotFoundException(updatedBook.getAuthor().getId().toString()));
    if (!user.getBooks().contains(bookId)) {
      user.getBooks().add(updatedBook);
    }
    userRepository.save(user);
    return new BookDto(bookRepository.save(updatedBook));
  }

  @CacheEvict(value = "book", key = "#bookId.hashCode()")
  @Transactional
  public void delete(Long bookId) {
    log.info("Удаление книги с ID: {}", bookId);
    BookEntity book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException(bookId.toString()));
    UserEntity user = userRepository.findById(book.getAuthor().getId()).orElseThrow(() -> new UserNotFoundException(bookId.toString()));
    user.getBooks().remove(bookId);
    bookRepository.delete(bookRepository.getById(bookId));
  }
}
