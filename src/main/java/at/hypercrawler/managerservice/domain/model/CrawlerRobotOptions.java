package at.hypercrawler.managerservice.domain.model;

import lombok.Builder;


@Builder
public record CrawlerRobotOptions(
        boolean ignoreRobotRules,
        boolean ignoreRobotNoIndex,
        boolean ignoreRobotNoFollowTo
) {
}
