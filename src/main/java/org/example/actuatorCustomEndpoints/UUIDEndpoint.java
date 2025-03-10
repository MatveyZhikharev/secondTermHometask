package org.example.actuatorCustomEndpoints;


import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Endpoint(id = "useless-line")
public class UUIDEndpoint {
  @ReadOperation
  public UUID customInfo() {
    return UUID.randomUUID();
  }
}