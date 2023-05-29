package at.hypercrawler.managerservice.domain.model;

import lombok.Builder;


@Builder
public record CrawlerRobotOptions(
        Boolean ignoreRobotRules,
        Boolean ignoreRobotNoIndex,
        Boolean ignoreRobotNoFollowTo
) {
}
