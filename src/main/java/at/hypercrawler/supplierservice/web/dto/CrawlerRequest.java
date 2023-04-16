package at.hypercrawler.supplierservice.web.dto;

import at.hypercrawler.supplierservice.domain.model.CrawlerConfig;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CrawlerRequest(@NotBlank(message = "Name must not be blank") String name,
                             @Valid @NotNull(message = "Crawler config must not be null") CrawlerConfig config) {
}
