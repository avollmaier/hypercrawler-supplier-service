package at.hypercrawler.managerservice.web.dto;

import at.hypercrawler.managerservice.domain.model.CrawlerConfig;
import at.hypercrawler.managerservice.domain.model.CrawlerStatus;

import java.time.Instant;
import java.util.UUID;

public record CrawlerResponse(
        UUID id, String name,
        CrawlerStatus status,
        CrawlerConfig config,
        Instant createdAt,
        Instant updatedAt
) {
}
