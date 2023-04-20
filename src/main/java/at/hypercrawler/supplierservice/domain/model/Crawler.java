package at.hypercrawler.supplierservice.domain.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Document(value = "crawler")
public record Crawler(@Id UUID id, String name, CrawlerStatus status, CrawlerConfig config,
                      @CreatedDate Instant createdAt, @LastModifiedDate Instant updatedAt, @Version int version) {
    public Crawler(String name, CrawlerStatus crawlerStatus, CrawlerConfig config) {
        this(UUID.randomUUID(), name, crawlerStatus, config, Instant.now(), Instant.now(), 0);
    }
}
