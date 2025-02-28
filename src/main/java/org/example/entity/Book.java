package org.example.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(name = "Book", description = "Книга")
public class Book {
  @Schema(name = "BookId", description = "Id")
  BookId id;
  @Schema(name = "Title", description = "Название книги")
  String title;
  @Schema(name = "AuthorId", description = "Автор")
  UserId authorId;
}
