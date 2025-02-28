package org.example.controller;

import org.example.entity.Book;
import org.example.entity.BookId;
import org.example.entity.UserId;
import org.example.request.BookCreateRequest;
import org.example.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookControllerImpl.class)
@ExtendWith(SpringExtension.class)
class BookControllerImplTest {
  @Autowired
  MockMvc mvc;

  @MockitoBean
  private BookService bookService;

  @Test
  void getAllBooks() throws Exception {
    when(bookService.getAll()).thenReturn(
        List.of(
            new Book(new BookId(1), "book1", new UserId(1)),
            new Book(new BookId(2), "book2", new UserId(1))
        )
    );
    mvc.perform(get("/api/books/"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  void getBookById() throws Exception {
    when(bookService.getById(new BookId(1)))
        .thenReturn(new Book(new BookId(1), "book1", new UserId(1)));
    mvc.perform(get("/api/books/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id.id").value(1))
        .andExpect(jsonPath("$.title").value("book1"))
        .andExpect(jsonPath("$.authorId.id").value(1));
  }
}
