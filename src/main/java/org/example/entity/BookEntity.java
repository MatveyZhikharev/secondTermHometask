package org.example.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "books")
@Schema(name = "Book", description = "Книга")
public class BookEntity {
  @Id
  @Schema(name = "BookId", description = "Id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Schema(name = "Title", description = "Название книги")
  @Column(name = "title", length = 64)
  @NotBlank
  private String title;

  @Schema(name = "AuthorId", description = "Автор")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id")
  private UserEntity author;
}
