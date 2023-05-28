package at.hypercrawler.managerservice.web;

import at.hypercrawler.managerservice.domain.model.*;
import at.hypercrawler.managerservice.web.dto.CrawlerRequest;
import at.hypercrawler.managerservice.web.dto.CrawlerResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CrawlerResponseJsonTest {

  Supplier<List<String>> startUrls = () -> Arrays.asList("https://www.google.com", "https://www.bing.com");
  Supplier<List<SupportedFileType>> fileTypesToMatch =
    () -> Arrays.asList(SupportedFileType.HTML, SupportedFileType.PDF);
  Supplier<List<String>> pathsToMatch = () -> List.of("http://www.foufos.gr/**");
  Supplier<List<String>> selectorsToMatch = () -> Arrays.asList(".products", "!.featured");

  public static Supplier<CrawlerFilterOptions> crawlerFilterOptions =
          () -> CrawlerFilterOptions.builder().queryParameterExclusionPatterns(Collections.singletonList("utm_*"))
                  .siteExclusionPatterns(Collections.singletonList("https://www.google.com/**")).build();
  Supplier<CrawlerAction> crawlerAction =
          () -> CrawlerAction.builder().fileTypesToMatch(fileTypesToMatch.get()).pathsToMatch(pathsToMatch.get())
                  .selectorsToMatch(selectorsToMatch.get()).indexName("test_index").build();
  Supplier<CrawlerRequestOptions> crawlerRequestOptions =
          () -> CrawlerRequestOptions.builder().requestTimeout(1000).proxy("http://localhost:8080").retries(3)
                  .headers(Collections.singletonList(new ConnectionHeader("User-Agent", "Mozilla/5.0 (compatible"))).build();
  Supplier<CrawlerRobotOptions> robotOptions =
          () -> CrawlerRobotOptions.builder().ignoreRobotNoFollowTo(true).ignoreRobotRules(true)
                  .ignoreRobotNoIndex(true).build();
  Supplier<CrawlerConfig> crawlerConfig =
          () -> CrawlerConfig.builder().actions(Collections.singletonList(crawlerAction.get()))
                  .indexPrefix("crawler_").requestOptions(crawlerRequestOptions.get()).startUrls(startUrls.get())
                  .schedule("0 0 0 1 1 ? 2099").robotOptions(robotOptions.get())
                  .filterOptions(crawlerFilterOptions.get()).build();
  Supplier<CrawlerRequest> crawlerRequest = () -> new CrawlerRequest("Test Crawler", crawlerConfig.get());
  @Autowired
  private JacksonTester<CrawlerResponse> json;

  @Test
  void testSerialize()
    throws Exception {
    var crawler =
            new CrawlerResponse(UUID.randomUUID(), "Test Crawler", CrawlerStatus.CREATED, crawlerConfig.get(),
                    Instant.now(), Instant.now());
    var jsonContent = json.write(crawler);
    assertThat(jsonContent).extractingJsonPathStringValue("@.id").isEqualTo(crawler.id().toString());
    assertThat(jsonContent).extractingJsonPathStringValue("@.name").isEqualTo(crawler.name());
    assertThat(jsonContent).extractingJsonPathStringValue("@.status")
            .isEqualTo(crawler.status().toString());

    assertThat(jsonContent).extractingJsonPathStringValue("@.config.indexPrefix")
            .isEqualTo(crawler.config().indexPrefix());
    assertThat(jsonContent).extractingJsonPathStringValue("@.config.schedule")
            .isEqualTo(crawler.config().schedule());

    assertThat(jsonContent).extractingJsonPathStringValue("@.config.startUrls[0]")
            .isEqualTo(crawler.config().startUrls().get(0));
    assertThat(jsonContent).extractingJsonPathStringValue("@.config.startUrls[1]")
            .isEqualTo(crawler.config().startUrls().get(1));

    assertThat(jsonContent).extractingJsonPathStringValue("@.config.filterOptions.siteExclusionPatterns[0]")
            .isEqualTo(crawler.config().filterOptions().siteExclusionPatterns().get(0));
    assertThat(jsonContent).extractingJsonPathStringValue("@.config.filterOptions.queryParameterExclusionPatterns[0]")
            .isEqualTo(crawler.config().filterOptions().queryParameterExclusionPatterns().get(0));

    assertThat(jsonContent).extractingJsonPathStringValue("@.config.requestOptions.proxy")
            .isEqualTo(crawler.config().requestOptions().proxy());
    assertThat(jsonContent).extractingJsonPathNumberValue("@.config.requestOptions.requestTimeout")
            .isEqualTo(crawler.config().requestOptions().requestTimeout());
    assertThat(jsonContent).extractingJsonPathNumberValue("@.config.requestOptions.retries")
            .isEqualTo(crawler.config().requestOptions().retries());
    assertThat(jsonContent).extractingJsonPathStringValue("@.config.requestOptions.headers[0].name")
            .isEqualTo(crawler.config().requestOptions().headers().get(0).name());
    assertThat(jsonContent).extractingJsonPathStringValue("@.config.requestOptions.headers[0].value")
            .isEqualTo(crawler.config().requestOptions().headers().get(0).value());

    assertThat(jsonContent).extractingJsonPathBooleanValue("@.config.robotOptions.ignoreRobotNoFollowTo")
            .isEqualTo(crawler.config().robotOptions().ignoreRobotNoFollowTo());
    assertThat(jsonContent).extractingJsonPathBooleanValue("@.config.robotOptions.ignoreRobotRules")
            .isEqualTo(crawler.config().robotOptions().ignoreRobotRules());
    assertThat(jsonContent).extractingJsonPathBooleanValue("@.config.robotOptions.ignoreRobotNoIndex")
            .isEqualTo(crawler.config().robotOptions().ignoreRobotNoIndex());

    assertThat(jsonContent).extractingJsonPathStringValue("@.config.actions[0].indexName")
            .isEqualTo(crawler.config().actions().get(0).indexName());
    assertThat(jsonContent).extractingJsonPathStringValue("@.config.actions[0].fileTypesToMatch[0]")
            .isEqualTo(crawler.config().actions().get(0).fileTypesToMatch().get(0).toString());
    assertThat(jsonContent).extractingJsonPathStringValue("@.config.actions[0].fileTypesToMatch[1]")
            .isEqualTo(crawler.config().actions().get(0).fileTypesToMatch().get(1).toString());
    assertThat(jsonContent).extractingJsonPathStringValue("@.config.actions[0].pathsToMatch[0]")
            .isEqualTo(crawler.config().actions().get(0).pathsToMatch().get(0));
    assertThat(jsonContent).extractingJsonPathStringValue("@.config.actions[0].selectorsToMatch[0]")
            .isEqualTo(crawler.config().actions().get(0).selectorsToMatch().get(0));
    assertThat(jsonContent).extractingJsonPathStringValue("@.config.actions[0].selectorsToMatch[1]")
            .isEqualTo(crawler.config().actions().get(0).selectorsToMatch().get(1));
  }

}
