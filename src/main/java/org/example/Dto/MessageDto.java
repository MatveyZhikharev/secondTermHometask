package org.example.Dto;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public class MessageDto {
  private UUID userId;
  private Instant timestamp;
  private String type;
  private String log;

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
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
}
