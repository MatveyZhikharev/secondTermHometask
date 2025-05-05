package org.example.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entity.BookEntity;
import org.example.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
  private Long id;
  private String name;
  private String surname;
  private List<Long> books;

  public UserDto(UserEntity user) {
    this.id = user.getId();
    this.name = user.getName();
    this.surname = user.getSurname();
    this.books = new ArrayList<>();
    for (BookEntity bookEntity : user.getBooks()) {
      this.books.add(bookEntity.getId());
    }
  }
}
