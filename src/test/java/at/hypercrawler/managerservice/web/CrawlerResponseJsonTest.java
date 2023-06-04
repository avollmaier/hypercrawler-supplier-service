package at.hypercrawler.managerservice.web;

import at.hypercrawler.managerservice.CrawlerTestDummyProvider;
import at.hypercrawler.managerservice.domain.model.CrawlerStatus;
import at.hypercrawler.managerservice.web.dto.CrawlerResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CrawlerResponseJsonTest {

  @Autowired
  private JacksonTester<CrawlerResponse> json;

  @Test
  void whenSerialize_thenValidSerializedJson()
          throws Exception {
    var crawler =
            new CrawlerResponse(UUID.randomUUID(), "Test Crawler", CrawlerStatus.CREATED, CrawlerTestDummyProvider.crawlerConfig.get(),
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

    assertThat(jsonContent).extractingJsonPathStringValue("@.config.requestOptions.proxy.host")
            .isEqualTo(crawler.config().requestOptions().proxy().host());
    assertThat(jsonContent).extractingJsonPathNumberValue("@.config.requestOptions.proxy.port")
            .isEqualTo(crawler.config().requestOptions().proxy().port());

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
      assertThat(jsonContent).extractingJsonPathStringValue("@.config.actions[0].contentTypesToMatch[0]")
              .isEqualTo(crawler.config().actions().get(0).contentTypesToMatch().get(0).toString());
      assertThat(jsonContent).extractingJsonPathStringValue("@.config.actions[0].contentTypesToMatch[1]")
              .isEqualTo(crawler.config().actions().get(0).contentTypesToMatch().get(1).toString());
      assertThat(jsonContent).extractingJsonPathStringValue("@.config.actions[0].pathsToMatch[0]")
              .isEqualTo(crawler.config().actions().get(0).pathsToMatch().get(0));
      assertThat(jsonContent).extractingJsonPathStringValue("@.config.actions[0].selectorsToMatch[0]")
              .isEqualTo(crawler.config().actions().get(0).selectorsToMatch().get(0));
      assertThat(jsonContent).extractingJsonPathStringValue("@.config.actions[0].selectorsToMatch[1]")
              .isEqualTo(crawler.config().actions().get(0).selectorsToMatch().get(1));
  }

}
