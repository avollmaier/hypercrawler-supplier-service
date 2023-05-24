package at.hypercrawler.managerservice.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import at.hypercrawler.managerservice.web.dto.CrawlerAction;
import at.hypercrawler.managerservice.web.dto.CrawlerConfig;
import at.hypercrawler.managerservice.web.dto.CrawlerRequest;
import at.hypercrawler.managerservice.web.dto.CrawlerRequestOptions;
import at.hypercrawler.managerservice.web.dto.CrawlerResponse;
import at.hypercrawler.managerservice.web.dto.CrawlerRobotOptions;
import at.hypercrawler.managerservice.web.dto.CrawlerStatus;
import at.hypercrawler.managerservice.web.dto.Header;
import at.hypercrawler.managerservice.web.dto.SupportedFileType;

@JsonTest
class CrawlerResponseJsonTest {

  Supplier<List<String>> startUrls = () -> Arrays.asList("https://www.google.com", "https://www.bing.com");
  Supplier<List<SupportedFileType>> fileTypesToMatch =
    () -> Arrays.asList(SupportedFileType.HTML, SupportedFileType.PDF);
  Supplier<List<String>> pathsToMatch = () -> List.of("http://www.foufos.gr/**");
  Supplier<List<String>> selectorsToMatch = () -> Arrays.asList(".products", "!.featured");

  Supplier<CrawlerAction> crawlerAction =
    () -> CrawlerAction.builder().fileTypesToMatch(fileTypesToMatch.get()).pathsToMatch(pathsToMatch.get())
      .selectorsToMatch(selectorsToMatch.get()).indexName("test_index").build();
  Supplier<CrawlerRequestOptions> crawlerRequestOptions =
    () -> CrawlerRequestOptions.builder().requestTimeout(1000).proxy("http://localhost:8080").retries(3)
      .headers(Collections.singletonList(new Header("User-Agent", "Mozilla/5.0 (compatible"))).build();
  Supplier<CrawlerRobotOptions> robotOptions =
    () -> CrawlerRobotOptions.builder().ignoreRobotNoFollowTo(true).ignoreRobotRules(true)
      .ignoreRobotNoIndex(true).build();
  Supplier<CrawlerConfig> crawlerConfig =
    () -> CrawlerConfig.builder().actions(Collections.singletonList(crawlerAction.get()))
      .indexPrefix("crawler_").requestOptions(crawlerRequestOptions.get()).startUrls(startUrls.get())
      .schedule("0 0 0 1 1 ? 2099").robotOptions(robotOptions.get())
      .queryParameterExclusionPatterns(Collections.singletonList("utm_*"))
      .siteExclusionPatterns(Collections.singletonList("https://www.google.com/**")).build();
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
    assertThat(jsonContent).extractingJsonPathStringValue("@.id").isEqualTo(crawler.getId().toString());
    assertThat(jsonContent).extractingJsonPathStringValue("@.name").isEqualTo(crawler.getName());
    assertThat(jsonContent).extractingJsonPathStringValue("@.status")
      .isEqualTo(crawler.getStatus().toString());

    assertThat(jsonContent).extractingJsonPathStringValue("@.config.indexPrefix")
      .isEqualTo(crawler.getConfig().getIndexPrefix());
    assertThat(jsonContent).extractingJsonPathStringValue("@.config.schedule")
      .isEqualTo(crawler.getConfig().getSchedule());

    assertThat(jsonContent).extractingJsonPathStringValue("@.config.startUrls[0]")
      .isEqualTo(crawler.getConfig().getStartUrls().get(0));
    assertThat(jsonContent).extractingJsonPathStringValue("@.config.startUrls[1]")
      .isEqualTo(crawler.getConfig().getStartUrls().get(1));

    assertThat(jsonContent).extractingJsonPathStringValue("@.config.siteExclusionPatterns[0]")
      .isEqualTo(crawler.getConfig().getSiteExclusionPatterns().get(0));
    assertThat(jsonContent).extractingJsonPathStringValue("@.config.queryParameterExclusionPatterns[0]")
      .isEqualTo(crawler.getConfig().getQueryParameterExclusionPatterns().get(0));

    assertThat(jsonContent).extractingJsonPathStringValue("@.config.requestOptions.proxy")
      .isEqualTo(crawler.getConfig().getRequestOptions().getProxy());
    assertThat(jsonContent).extractingJsonPathNumberValue("@.config.requestOptions.requestTimeout")
      .isEqualTo(crawler.getConfig().getRequestOptions().getRequestTimeout());
    assertThat(jsonContent).extractingJsonPathNumberValue("@.config.requestOptions.retries")
      .isEqualTo(crawler.getConfig().getRequestOptions().getRetries());
    assertThat(jsonContent).extractingJsonPathStringValue("@.config.requestOptions.headers[0].name")
      .isEqualTo(crawler.getConfig().getRequestOptions().getHeaders().get(0).getName());
    assertThat(jsonContent).extractingJsonPathStringValue("@.config.requestOptions.headers[0].value")
      .isEqualTo(crawler.getConfig().getRequestOptions().getHeaders().get(0).getValue());

    assertThat(jsonContent).extractingJsonPathBooleanValue("@.config.robotOptions.ignoreRobotNoFollowTo")
      .isEqualTo(crawler.getConfig().getRobotOptions().isIgnoreRobotNoFollowTo());
    assertThat(jsonContent).extractingJsonPathBooleanValue("@.config.robotOptions.ignoreRobotRules")
      .isEqualTo(crawler.getConfig().getRobotOptions().isIgnoreRobotRules());
    assertThat(jsonContent).extractingJsonPathBooleanValue("@.config.robotOptions.ignoreRobotNoIndex")
      .isEqualTo(crawler.getConfig().getRobotOptions().isIgnoreRobotNoIndex());

    assertThat(jsonContent).extractingJsonPathStringValue("@.config.actions[0].indexName")
      .isEqualTo(crawler.getConfig().getActions().get(0).getIndexName());
    assertThat(jsonContent).extractingJsonPathStringValue("@.config.actions[0].fileTypesToMatch[0]")
      .isEqualTo(crawler.getConfig().getActions().get(0).getFileTypesToMatch().get(0).toString());
    assertThat(jsonContent).extractingJsonPathStringValue("@.config.actions[0].fileTypesToMatch[1]")
      .isEqualTo(crawler.getConfig().getActions().get(0).getFileTypesToMatch().get(1).toString());
    assertThat(jsonContent).extractingJsonPathStringValue("@.config.actions[0].pathsToMatch[0]")
      .isEqualTo(crawler.getConfig().getActions().get(0).getPathsToMatch().get(0));
    assertThat(jsonContent).extractingJsonPathStringValue("@.config.actions[0].selectorsToMatch[0]")
      .isEqualTo(crawler.getConfig().getActions().get(0).getSelectorsToMatch().get(0));
    assertThat(jsonContent).extractingJsonPathStringValue("@.config.actions[0].selectorsToMatch[1]")
      .isEqualTo(crawler.getConfig().getActions().get(0).getSelectorsToMatch().get(1));
  }

}
