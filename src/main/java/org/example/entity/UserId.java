package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserId {
  private int id;

  public UserId(String stringId) {
    this.id = Integer.parseInt(stringId);
  }
}
