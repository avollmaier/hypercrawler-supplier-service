package at.hypercrawler.managerservice.web.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CrawlerRobotOptions {
  boolean ignoreRobotRules;
  boolean ignoreRobotNoIndex;
  boolean ignoreRobotNoFollowTo;
}
