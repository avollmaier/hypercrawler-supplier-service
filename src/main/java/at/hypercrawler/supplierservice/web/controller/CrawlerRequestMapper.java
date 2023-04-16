package at.hypercrawler.supplierservice.web.controller;

import at.hypercrawler.supplierservice.domain.model.Crawler;
import at.hypercrawler.supplierservice.domain.model.CrawlerStatus;
import at.hypercrawler.supplierservice.web.dto.CrawlerRequest;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class CrawlerRequestMapper
        implements Function<CrawlerRequest, Crawler> {

    @Override
    public Crawler apply(CrawlerRequest crawlerRequest) {
        // @formatter:off
        return new Crawler(
                crawlerRequest.name(),
                CrawlerStatus.CREATED,
                crawlerRequest.config()
        );
        // @formatter:on
    }
}
