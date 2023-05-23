package at.hypercrawler.managerservice.domain.model;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import at.hypercrawler.managerservice.web.dto.CrawlerConfig;
import at.hypercrawler.managerservice.web.dto.CrawlerStatus;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
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

}
