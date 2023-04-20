package at.hypercrawler.managerservice.web.controller;

import at.hypercrawler.managerservice.domain.model.Crawler;
import at.hypercrawler.managerservice.domain.model.CrawlerStatus;
import at.hypercrawler.managerservice.web.dto.CrawlerRequest;
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
