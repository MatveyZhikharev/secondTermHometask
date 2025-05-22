package org.example.controller;

import org.example.Application;
import org.example.Dto.BookDto;
import org.example.entity.BookEntity;
import org.example.entity.UserEntity;
import org.example.repository.UserRepository;
import org.example.security.WebSecurityConfig;
import org.example.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookControllerImpl.class)
@ContextConfiguration(classes={Application.class, WebSecurityConfig.class})
@ExtendWith(SpringExtension.class)
class BookControllerImplTest {
  @Autowired
  MockMvc mvc;

  @MockitoBean
  private BookService bookService;

  @MockitoBean
  private UserRepository userRepository;

  @Test
  void getAllBooks() throws Exception {
    when(bookService.getAll(0L)).thenReturn(
        List.of(
            new BookDto(1L, "book1", 1L),
            new BookDto(2L, "book2", 1L)
        )
    );
    mvc.perform(get("/api/books/"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  void getBookById() throws Exception {
    when(bookService.getById(0L, 1L))
        .thenReturn(new BookDto(new BookEntity(1L, "book1", new UserEntity(1L, "", "", new ArrayList<>()))));
    mvc.perform(get("/api/books/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.title").value("book1"))
        .andExpect(jsonPath("$.authorId").value(1));
  }

  @Test
  void deleteBook() throws Exception {
    bookService.create(0L, "", 1L);
    mvc.perform(delete("/api/books/1"))
        .andExpect(status().isNoContent());
  }
}
