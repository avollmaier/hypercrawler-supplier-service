package at.hypercrawler.supplierservice.domain.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Document(value = "crawler")
public record Crawler (
    @Id
     UUID id,
     String name,
     CrawlerStatus status,
    @CreatedDate
     Instant createdAt,
    @LastModifiedDate
     Instant updatedAt

){
     public Crawler(String testCrawler, CrawlerStatus crawlerStatus) {
            this(UUID.randomUUID(), testCrawler, crawlerStatus, Instant.now(), Instant.now());
     }
}
