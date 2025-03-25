package org.example.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Schema(name = "users", description = "Пользователь")
public class User {
  @Schema(name = "UserId", description = "Id")
  UserId id;
  @Schema(name = "Name", description = "Имя")
  String name;
  @Schema(name = "Surname", description = "Фамилия")
  String surname;
  @Schema(name = "Books", description = "Книги")
  List<BookId> books;
}
