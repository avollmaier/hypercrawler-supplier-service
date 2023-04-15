package at.hypercrawler.supplierservice.web.controller;

import at.hypercrawler.supplierservice.web.dto.CrawlerRequest;
import at.hypercrawler.supplierservice.domain.model.Crawler;
import at.hypercrawler.supplierservice.domain.model.CrawlerStatus;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Function;

@Component
public class CrawlerRequestMapper implements Function<CrawlerRequest, Crawler> {

    @Override
    public Crawler apply(CrawlerRequest crawlerRequest) {
        // @formatter:off
        return new Crawler(
                UUID.randomUUID(),
                crawlerRequest.name(),
                CrawlerStatus.CREATED,
                Instant.now(),
                Instant.now()
        );
        // @formatter:on
    }
}
