package at.hypercrawler.managerservice.web.controller;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import at.hypercrawler.managerservice.domain.model.Crawler;
import at.hypercrawler.managerservice.web.dto.CrawlerResponse;

@Component
public class CrawlerResponseMapper
  implements Function<Crawler, CrawlerResponse> {
  @Override
  public CrawlerResponse apply(Crawler crawler) {
    // @formatter:off
    return new CrawlerResponse(
            crawler.getId(),
            crawler.getName(),
            crawler.getStatus(),
            crawler.getConfig(),
            crawler.getCreatedAt(),
            crawler.getUpdatedAt()
    );
    // @formatter:on
  }
}
