package org.example.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
@Schema(description = "Модель запроса частичного обновления пользователя")
public class UserPatchRequest {
  @Schema(description = "ID пользователя")
  private Long id;

  @Size(min = 2, max = 16)
  @Schema(description = "Имя")
  private String name;

  @Size(min = 1, max = 16)
  @Schema(description = "Фамилия")
  private String surname;

  @Schema(description = "Книги")
  private List<Long> books;
}
