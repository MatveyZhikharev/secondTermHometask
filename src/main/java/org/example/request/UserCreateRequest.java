package org.example.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.entity.BookId;
import org.example.entity.UserId;

import java.util.List;

@AllArgsConstructor
@Data
@Schema(description = "Модель запроса создания пользователя")
public class UserCreateRequest {
  @Schema(description = "Уникальный идентификатор пользователя")
  private UserId id;

  @NotBlank
  @Size(min = 2, max = 16)
  @Schema(description = "Имя")
  private String name;

  @NotBlank
  @Size(min = 1, max = 16)
  @Schema(description = "Фамилия")
  private String surname;

  @Schema(description = "Книги")
  private List<BookId> books;
}
