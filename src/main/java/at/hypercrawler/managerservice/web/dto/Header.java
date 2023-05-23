package at.hypercrawler.managerservice.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class Header {
  @NotNull(message = "Request header name could not be null") String name;
  @NotNull(message = "Request header value could not be null") String value;
}
