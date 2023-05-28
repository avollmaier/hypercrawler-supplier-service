package at.hypercrawler.managerservice.web.dto;

import at.hypercrawler.managerservice.domain.model.CrawlerStatus;


public record StatusResponse(CrawlerStatus status) {
}
