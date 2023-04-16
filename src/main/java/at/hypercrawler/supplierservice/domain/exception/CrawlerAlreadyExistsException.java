package at.hypercrawler.supplierservice.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

public class CrawlerAlreadyExistsException
        extends ResponseStatusException {
    public CrawlerAlreadyExistsException(UUID uuid) {
        super(HttpStatus.CONFLICT, "Crawler with id " + uuid + " already exists");
    }
}
