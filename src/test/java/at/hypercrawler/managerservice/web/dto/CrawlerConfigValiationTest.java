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

class CrawlerConfigValiationTest {

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
  Supplier<String> validCron = () -> "0 0 12 * * ?";
  //supplier with prefilled builder
  Supplier<CrawlerConfig.CrawlerConfigBuilder> crawlerConfigBuilder =
    () -> CrawlerConfig.builder().actions(Collections.singletonList(crawlerAction.get()))
      .indexPrefix("crawler_").requestOptions(crawlerRequestOptions.get()).startUrls(startUrls.get())
      .schedule(validCron.get()).robotOptions(robotOptions.get())
      .queryParameterExclusionPatterns(Collections.singletonList("utm_*"))
      .siteExclusionPatterns(Collections.singletonList("https://www.google.com/**"));

  @BeforeAll
  public static void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void whenIndexPrefixIsNullThenValidationFails() {
    var crawlerConfig = crawlerConfigBuilder.get().indexPrefix(null).build();
    var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
    Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
    assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
      .containsExactly("Index prefix could not be empty");
  }

  @Test
  void whenScheduleIsNullThenValidationSucceeds() {
    var crawlerConfig = crawlerConfigBuilder.get().schedule(null).build();
    var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
    Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
    assertThat(violations).isEmpty();
  }

  @Test
  void whenScheduleDoesntMatchPatternThenValidationFails() {
    var crawlerConfig = crawlerConfigBuilder.get().schedule("invalid cron").build();
    var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
    Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
    assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
      .containsExactly("Schedule is not valid");
  }

  @Test
  void whenScheduleMatchesPatternThenValidationSucceeds() {
    var crawlerConfig = crawlerConfigBuilder.get().schedule(validCron.get()).build();
    var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
    Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
    assertThat(violations).isEmpty();
  }

  @Test
  void whenStartUrlsIsNullThenValidationFails() {
    var crawlerConfig = crawlerConfigBuilder.get().startUrls(null).build();
    var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
    Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
    assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
      .containsExactly("Start-Urls could not be empty");
  }

  @Test
  void whenAddedStartUrlIsNullThenValidationFails() {
    var startUrls = Arrays.asList("https://www.google.com", null);
    var crawlerConfig = crawlerConfigBuilder.get().startUrls(startUrls).build();
    var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
    Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
    assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
      .containsExactly("Start-Url could not be empty");
  }

  @Test
  void whenAddedStartUrlIsEmptyThenValidationFails() {
    var startUrls = Arrays.asList("https://www.google.com", "");
    var crawlerConfig = crawlerConfigBuilder.get().startUrls(startUrls).build();
    var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
    Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
    assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
      .containsExactly("Start-Url could not be empty");
  }

  @Test
  void whenSiteExclusionPatternsIsNullThenValidationFails() {
    var crawlerConfig = crawlerConfigBuilder.get().siteExclusionPatterns(null).build();
    var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
    Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
    assertThat(violations).isEmpty();
  }

  @Test
  void whenAddedSiteExclusionPatternIsNullThenValidationFails() {
    var siteExclusionPatterns = Arrays.asList("https://www.google.com", null);
    var crawlerConfig = crawlerConfigBuilder.get().siteExclusionPatterns(siteExclusionPatterns).build();
    var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
    Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
    assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
      .containsExactly("Exclusion-Pattern could not be empty");
  }

  @Test
  void whenAddedSiteExclusionPatternIsEmptyThenValidationFails() {
    var siteExclusionPatterns = Arrays.asList("https://www.google.com", "");
    var crawlerConfig = crawlerConfigBuilder.get().siteExclusionPatterns(siteExclusionPatterns).build();
    var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
    Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
    assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
      .containsExactly("Exclusion-Pattern could not be empty");
  }

  @Test
  void whenQueryParameterExclusionPatternsIsNullThenValidationSucceeds() {
    var crawlerConfig = crawlerConfigBuilder.get().queryParameterExclusionPatterns(null).build();
    var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
    Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
    assertThat(violations).isEmpty();
  }

  @Test
  void whenAddedQueryParameterExclusionPatternIsNullThenValidationFails() {
    var queryParameterExclusionPatterns = Arrays.asList("utm_*", null);
    var crawlerConfig =
      crawlerConfigBuilder.get().queryParameterExclusionPatterns(queryParameterExclusionPatterns).build();
    var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
    Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
    assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
      .containsExactly("Ignored-Query-Parameter could not be empty");
  }

  @Test
  void whenAddedQueryParameterExclusionPatternIsEmptyThenValidationFails() {
    var queryParameterExclusionPatterns = Arrays.asList("utm_*", "");
    var crawlerConfig =
      crawlerConfigBuilder.get().queryParameterExclusionPatterns(queryParameterExclusionPatterns).build();
    var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
    Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
    assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
      .containsExactly("Ignored-Query-Parameter could not be empty");
  }

  @Test
  void whenRequestOptionsIsNullThenValidationSucceeds() {
    var crawlerConfig = crawlerConfigBuilder.get().requestOptions(null).build();
    var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
    Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
    assertThat(violations).isEmpty();
  }

  @Test
  void whenRequestOptionRequestTimeoutIsLowerThanZeroThenValidationFails() {
    var requestOptions = CrawlerRequestOptions.builder().requestTimeout(-1).retries(1).build();
    var crawlerConfig = crawlerConfigBuilder.get().requestOptions(requestOptions).build();
    var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
    Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
    assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
      .containsExactly("Request timeout must be greater than 0");
  }

  @Test
  void whenRequestOptionRetriesIsLowerThanZeroThenValidationFails() {
    var requestOptions = CrawlerRequestOptions.builder().retries(-1).requestTimeout(12).build();
    var crawlerConfig = crawlerConfigBuilder.get().requestOptions(requestOptions).build();
    var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
    Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
    assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
      .containsExactly("Retries must be greater than 0");
  }

  @Test
  void whenRequestOptionHeadersIsNullThenValidationSucceeds() {
    var requestOptions = CrawlerRequestOptions.builder().headers(null).requestTimeout(12).retries(12).build();
    var crawlerConfig = crawlerConfigBuilder.get().requestOptions(requestOptions).build();
    var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
    Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
    assertThat(violations).isEmpty();
  }

  @Test
  void whenAddedRequestOptionHeaderIsNullThenValidationFails() {
    var headers = Arrays.asList(new Header("name", "type"), null);
    var requestOptions =
      CrawlerRequestOptions.builder().requestTimeout(12).retries(12).headers(headers).build();
    var crawlerConfig = crawlerConfigBuilder.get().requestOptions(requestOptions).build();
    var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
    Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
    assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
      .containsExactly("Request header could not be null");
  }

  @Test
  void whenAddedRequestOptionHeaderNameIsNullThenValidationFails() {
    var headers = Arrays.asList(new Header(null, "type"));
    var requestOptions =
      CrawlerRequestOptions.builder().requestTimeout(12).retries(12).headers(headers).build();
    var crawlerConfig = crawlerConfigBuilder.get().requestOptions(requestOptions).build();
    var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
    Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);

    assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
      .containsExactly("Request header name could not be null");
  }

  @Test
  void whenAddedRequestOptionHeaderTypeIsNullThenValidationFails() {
    var headers = Arrays.asList(new Header("name", null));
    var requestOptions =
      CrawlerRequestOptions.builder().requestTimeout(12).retries(12).headers(headers).build();
    var crawlerConfig = crawlerConfigBuilder.get().requestOptions(requestOptions).build();
    var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
    Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);

    assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
      .containsExactly("Request header value could not be null");
  }

  @Test
  void whenRobotOptionsIsNullThenValidationSucceeds() {
    var crawlerConfig = crawlerConfigBuilder.get().robotOptions(null).build();
    var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
    Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
    assertThat(violations).isEmpty();
  }

  @Test
  void whenActionIsNullThenValidationFails() {
    var crawlerConfig = crawlerConfigBuilder.get().actions(null).build();
    var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
    Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
    assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
      .containsExactly("Actions could not be empty");
  }

  @Test
  void whenAddedActionIsNullThenValidationFails() {
    var actions = Arrays.asList(crawlerAction.get(), null);
    var crawlerConfig = crawlerConfigBuilder.get().actions(actions).build();
    var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
    Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
    assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
      .containsExactly("Actions could not be empty");
  }

}
