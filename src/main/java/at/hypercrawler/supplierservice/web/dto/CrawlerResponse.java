package at.hypercrawler.supplierservice.web.dto;

import at.hypercrawler.supplierservice.domain.model.CrawlerStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.UUID;


public record CrawlerResponse(UUID id, String name, CrawlerStatus status,

                              Instant createdAt,
                              Instant updatedAt) {
}
