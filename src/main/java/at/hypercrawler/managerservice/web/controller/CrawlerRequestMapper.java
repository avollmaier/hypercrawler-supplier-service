package at.hypercrawler.managerservice.web.controller;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import at.hypercrawler.managerservice.domain.model.Crawler;
import at.hypercrawler.managerservice.web.dto.CrawlerRequest;
import at.hypercrawler.managerservice.web.dto.CrawlerStatus;

@Component
public class CrawlerRequestMapper
        implements Function<CrawlerRequest, Crawler> {

    @Override
    public Crawler apply(CrawlerRequest crawlerRequest) {
        // @formatter:off
        return new Crawler(
                crawlerRequest.getName(),
                CrawlerStatus.CREATED,
                crawlerRequest.getConfig()
        );
        // @formatter:on
    }
}
