package org.example.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDto {
  private Long userId;
  private Instant timestamp;
  private String type;
  private String log;

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getLog() {
    return log;
  }

  public void setLog(String log) {
    this.log = log;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Instant timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MessageDto that = (MessageDto) o;
    return Objects.equals(userId, that.userId) && Objects.equals(timestamp, that.timestamp) && Objects.equals(type, that.type) && Objects.equals(log, that.log);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, timestamp, type, log);
  }
}
