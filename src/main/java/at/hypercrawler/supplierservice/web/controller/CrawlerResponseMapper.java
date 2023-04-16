package at.hypercrawler.supplierservice.web.controller;

import at.hypercrawler.supplierservice.domain.model.Crawler;
import at.hypercrawler.supplierservice.web.dto.CrawlerResponse;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class CrawlerResponseMapper
        implements Function<Crawler, CrawlerResponse> {
    @Override
    public CrawlerResponse apply(Crawler crawler) {
        // @formatter:off
            return new CrawlerResponse(
                    crawler.id(),
                    crawler.name(),
                    crawler.status(),
                    crawler.config(),
                    crawler.createdAt(),
                    crawler.updatedAt()            );
            // @formatter:on
    }
}
