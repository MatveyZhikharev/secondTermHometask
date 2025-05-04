package org.example.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entity.BookEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDto {
  private Long id;
  private String title;
  private Long authorId;

  public BookDto(BookEntity bookEntity) {
    this.id = bookEntity.getId();
    this.title = bookEntity.getTitle();
    this.authorId = bookEntity.getAuthor().getId();
  }
}
