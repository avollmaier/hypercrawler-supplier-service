package at.hypercrawler.managerservice.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

public class CrawlerNotFoundException extends ResponseStatusException {
    public CrawlerNotFoundException(UUID uuid) {
        super(HttpStatus.NOT_FOUND, "Crawler with id " + uuid + " not found");
    }
}
