package org.example.service;

import org.example.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BookService {
  private static final Logger log = LoggerFactory.getLogger(BookService.class);

  private final BookRepository bookRepository;

  public BookService(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  public void logCall() {
    log.info("Вызван метод BookService");
  }
}
