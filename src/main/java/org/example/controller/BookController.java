package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.entity.Book;
import org.example.entity.BookId;
import org.example.request.BookCreateRequest;
import org.example.request.BookPatchRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "BookControllerInterface", description = "Управление книгами")
@RequestMapping("/api/books")
public interface BookController {
  @Operation(summary = "Получить все книги")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Все книги получены",
          content = {@Content(
              mediaType = "application/json",
              array = @ArraySchema(schema = @Schema(implementation = Book.class))
          )
          })
  })
  @GetMapping("/")
  ResponseEntity<List<Book>> getAllBooks();

  @Operation(summary = "Получить книгу по ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Книга получена",
          content = {@Content(
              mediaType = "application/json",
              schema = @Schema(implementation = Book.class)
          )
          }),
      @ApiResponse(responseCode = "400", description = "Неверные данные запроса",
          content = {@Content}),
      @ApiResponse(responseCode = "404", description = "Книга не найдена",
          content = {@Content})
  })
  @GetMapping("/{id}")
  ResponseEntity<Book> getBookById(
      @Parameter(description = "ID ", required = true)
      @Valid
      @PathVariable("id") BookId id
  );

  @Operation(summary = "Создание книги")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Книга создана",
          content = {@Content(
              mediaType = "application/json",
              schema = @Schema(implementation = Long.class)
          )}),
      @ApiResponse(responseCode = "400", description = "Неверные данные запроса",
          content = {@Content}),
  })
  @PostMapping("/")
  ResponseEntity<BookId> createBook(
      @Parameter(description = "Данные о книге")
      @Valid @RequestBody BookCreateRequest bookDraft);

  @Operation(summary = "Полное обновление данных книги")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Книга обновлена",
          content = {@Content(
              mediaType = "application/json",
              schema = @Schema(implementation = Book.class)
          )
          }),
      @ApiResponse(responseCode = "404", description = "Книга не найдена",
          content = {@Content}),
      @ApiResponse(responseCode = "400", description = "Неверные данные запроса",
          content = {@Content})
  })
  @PatchMapping("/{id}")
  ResponseEntity<Book> updateBook(
      @Parameter(description = "ID книги") @Valid @PathVariable("id") BookId id,
      @Parameter(description = "Данные о книге") @Valid @RequestBody BookPatchRequest book);

  @Operation(summary = "Удаление книги")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Книга удалена",
          content = {@Content}),
      @ApiResponse(responseCode = "400", description = "Неверные данные запроса",
          content = {@Content})
  })
  @DeleteMapping("/{id}")
  ResponseEntity<Void> deleteBook(
      @Parameter(description = "ID книги") @Valid @PathVariable("id") BookId id
  );
}
