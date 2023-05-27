package at.hypercrawler.managerservice.web.dto;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import static org.assertj.core.api.Assertions.assertThat;

public class CrawlerRequestValiationTest {

  private static Validator validator;

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
          () -> CrawlerConfig.builder().action(crawlerAction.get())
      .indexPrefix("crawler_").requestOptions(crawlerRequestOptions.get()).startUrls(startUrls.get())
      .schedule("0 0 0 1 1 ? 2099").robotOptions(robotOptions.get()).build();
  Supplier<CrawlerRequest> crawlerRequest = () -> new CrawlerRequest("Test Crawler", crawlerConfig.get());

  @BeforeAll
  public static void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void whenAllFieldsCorrectThenValidationSucceeds() {
    Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest.get());
    assertThat(violations).isEmpty();
  }

  @Test
  void whenNameIsNullThenValidationFails() {
    var crawlerRequest = new CrawlerRequest(null, crawlerConfig.get());
    Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
    assertThat(violations).isNotEmpty();
  }

  @Test
  void whenNameIsEmptyThenValidationFails() {
    var crawlerRequest = new CrawlerRequest("", crawlerConfig.get());
    Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
    assertThat(violations).isNotEmpty();
  }

  @Test
  void whenConfigIsNullThenValidationFails() {
    var crawlerRequest = new CrawlerRequest("Test Crawler", null);
    Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
    assertThat(violations).isNotEmpty();
  }

}
