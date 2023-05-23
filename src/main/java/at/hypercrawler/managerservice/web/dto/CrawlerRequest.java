package at.hypercrawler.managerservice.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class CrawlerRequest {
      @NotBlank(message = "Name must not be blank") String name;
      @NotNull @Valid CrawlerConfig config;
}
