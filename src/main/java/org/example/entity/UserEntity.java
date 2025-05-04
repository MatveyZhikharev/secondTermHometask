package org.example.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@Schema(name = "User", description = "Пользователь")
public class UserEntity {
  @Id
  @Schema(name = "UserId", description = "Id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Schema(name = "Name", description = "Имя")
  @Column(name = "name")
  private String name;

  @Schema(name = "Surname", description = "Фамилия")
  @Column(name = "surname")
  private String surname;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "author")
  @Schema(name = "Books", description = "Книги")
  private List<BookEntity> books;
}
