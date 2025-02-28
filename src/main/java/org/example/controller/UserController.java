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
import org.example.entity.User;
import org.example.entity.UserId;
import org.example.request.UserCreateRequest;
import org.example.request.UserPutRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "UsersControllerInterface", description = "Управление пользователями")
@RequestMapping("api/users")
public interface UserController {
  @Operation(summary = "Получить всех пользователей")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Все пользователи получены",
          content = {@Content(
              mediaType = "application/json",
              array = @ArraySchema(schema = @Schema(implementation = User.class))
          )
          })
  })
  @GetMapping("/")
  ResponseEntity<List<User>> getAllUsers();

  @Operation(summary = "Получить пользователя по ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Пользователь получена",
          content = {@Content(
              mediaType = "application/json",
              schema = @Schema(implementation = User.class)
          )
          }),
      @ApiResponse(responseCode = "400", description = "Неверные данные запроса",
          content = {@Content}),
      @ApiResponse(responseCode = "404", description = "Пользователь не найдена",
          content = {@Content})
  })
  @GetMapping("/{id}")
  ResponseEntity<User> getUserById(
      @Parameter(description = "ID ", required = true)
      @Valid
      @PathVariable("id") UserId id
  );

  @Operation(summary = "Создание пользователя")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Пользователь создан",
          content = {@Content(
              mediaType = "application/json",
              schema = @Schema(implementation = Long.class)
          )}),
      @ApiResponse(responseCode = "400", description = "Неверные данные запроса",
          content = {@Content}),
  })
  @PostMapping("/")
  ResponseEntity<UserId> createUser(
      @Parameter(description = "Данные о пользователе")
      @Valid @RequestBody UserCreateRequest userDraft);

  @Operation(summary = "Полное обновление данных пользователя")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Пользователь обновлён",
          content = {@Content(
              mediaType = "application/json",
              schema = @Schema(implementation = User.class)
          )
          }),
      @ApiResponse(responseCode = "404", description = "Пользователь не найден",
          content = {@Content}),
      @ApiResponse(responseCode = "400", description = "Неверные данные запроса",
          content = {@Content})
  })
  @PatchMapping("/{id}")
  ResponseEntity<User> updateUser(
      @Parameter(description = "ID пользователя") @Valid @PathVariable("id") UserId id,
      @Parameter(description = "Данные о пользователе") @Valid @RequestBody UserPutRequest user);

  @Operation(summary = "Удаление пользователя")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Пользователь удалён",
          content = {@Content}),
      @ApiResponse(responseCode = "400", description = "Неверные данные запроса",
          content = {@Content})
  })
  @DeleteMapping("/{id}")
  ResponseEntity<Void> deleteUser(
      @Parameter(description = "ID пользователя") @Valid @PathVariable("id") UserId id
  );
}