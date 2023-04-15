package at.hypercrawler.supplierservice.domain.exception;

import java.util.UUID;

public class CrawlerAlreadyExistsException extends RuntimeException{
    public CrawlerAlreadyExistsException(UUID uuid) {
        super("Crawler with id " + uuid + " already exists");
    }
}
