package at.hypercrawler.managerservice.web.dto;

import java.time.Instant;
import java.util.UUID;

import lombok.Value;

@Value
public class CrawlerResponse {
  UUID id;
  String name;
  CrawlerStatus status;
  CrawlerConfig config;
  Instant createdAt;
  Instant updatedAt;
}
