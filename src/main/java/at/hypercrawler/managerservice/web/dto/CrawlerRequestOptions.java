package at.hypercrawler.managerservice.web.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CrawlerRequestOptions {
  String proxy;

  @Min(value = 1, message = "Request timeout must be greater than 0") int requestTimeout;

  @Min(value = 1, message = "Retries must be greater than 0") int retries;

  List<@Valid @NotNull(message = "Request header could not be null") Header> headers;
}
