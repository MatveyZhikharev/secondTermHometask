package org.example.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import static jakarta.persistence.GenerationType.IDENTITY;

@Table(name = "outbox")
@Entity
@Getter
@Setter
public class OutboxRecord {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  @NotNull
  private String data;

  public OutboxRecord(String data) {
    this.data = data;
  }

  public OutboxRecord() {
  }
}
