package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookId {
  private int id;

  public BookId(String stringId) {
    this.id = Integer.parseInt(stringId);
  }
}
