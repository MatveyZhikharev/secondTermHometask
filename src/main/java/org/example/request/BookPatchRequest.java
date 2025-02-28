package org.example.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.entity.BookId;
import org.example.entity.UserId;

@AllArgsConstructor
@Data
@Schema(description = "Модель запроса обновления книги")
public class BookPatchRequest {
  @Schema(description = "ID книги")
  private BookId id;

  @Size(min = 2, max = 64)
  @Schema(description = "Название книги")
  private String title;

  @NotNull
  @Schema(description = "ID пользователя, связанного с книгой")
  private UserId userId;
}
