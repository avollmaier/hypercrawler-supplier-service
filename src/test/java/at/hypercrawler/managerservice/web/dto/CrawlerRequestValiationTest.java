package at.hypercrawler.managerservice.web.dto;

import at.hypercrawler.managerservice.CrawlerTestDummyProvider;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class CrawlerRequestValiationTest {

  private static Validator validator;

  @BeforeAll
  public static void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void whenAllFieldsCorrect_thenValidationSucceeds() {
    Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(CrawlerTestDummyProvider.crawlerRequest.get());
    assertThat(violations).isEmpty();
  }

  @Test
  void whenNameIsNull_thenValidationFails() {
    var crawlerRequest = new CrawlerRequest(null, CrawlerTestDummyProvider.crawlerConfig.get());
    Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
    assertThat(violations).isNotEmpty();
  }

  @Test
  void whenNameIsEmpty_thenValidationFails() {
    var crawlerRequest = new CrawlerRequest("", CrawlerTestDummyProvider.crawlerConfig.get());
    Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
    assertThat(violations).isNotEmpty();
  }

  @Test
  @SuppressWarnings("all")
  void whenConfigIsNull_thenValidationFails() {
    var crawlerRequest = new CrawlerRequest("Test Crawler", null);
    Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
    assertThat(violations).isNotEmpty();
  }

}
