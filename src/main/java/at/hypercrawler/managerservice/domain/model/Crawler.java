package at.hypercrawler.managerservice.domain.model;

import java.time.Instant;
import java.util.UUID;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import at.hypercrawler.managerservice.web.dto.CrawlerConfig;
import at.hypercrawler.managerservice.web.dto.CrawlerStatus;

@Data
@NoArgsConstructor
@Document(value = "crawler")
public class Crawler {
  @Id
  UUID id;

  String name;

  CrawlerConfig config;

  CrawlerStatus status;

  @CreatedDate
  Instant createdAt;

  @LastModifiedDate
  Instant updatedAt;

  @Version
  int version;

  public Crawler(String name, CrawlerStatus status, CrawlerConfig config) {
    this(UUID.randomUUID(), name, config, status, Instant.now(), Instant.now(), 0);
  }

  public Crawler(UUID id, String name, CrawlerConfig config, CrawlerStatus status, Instant createdAt, Instant updatedAt, int version) {
    this.id = id;
    this.name = name;
    this.config = config;
    this.status = status;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.version = version;
  }
}
