package at.hypercrawler.supplierservice.domain.exception;

import java.util.UUID;

public class CrawlerNotFoundException extends RuntimeException {
    public CrawlerNotFoundException(UUID uuid) {
        super("Crawler with id " + uuid + " not found");
    }
}
