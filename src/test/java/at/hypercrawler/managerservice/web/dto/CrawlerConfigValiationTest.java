package at.hypercrawler.managerservice.web.dto;

import at.hypercrawler.managerservice.CrawlerTestDummyProvider;
import at.hypercrawler.managerservice.domain.model.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("all")
class CrawlerConfigValiationTest {

    private static Validator validator;

    Supplier<String> validCron = () -> "0 0 12 * * ?";

    Supplier<CrawlerConfig.CrawlerConfigBuilder> crawlerConfigBuilder =
            () -> CrawlerConfig.builder().actions(Collections.singletonList(CrawlerTestDummyProvider.crawlerAction.get()))
                    .indexPrefix("crawler_").requestOptions(CrawlerTestDummyProvider.crawlerRequestOptions.get()).startUrls(CrawlerTestDummyProvider.startUrls.get())
                    .schedule(validCron.get()).robotOptions(CrawlerTestDummyProvider.robotOptions.get())
                    .filterOptions(CrawlerTestDummyProvider.crawlerFilterOptions.get());

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenIndexPrefixIsNull_thenValidationFails() {
        var crawlerConfig = crawlerConfigBuilder.get().indexPrefix(null).build();
        var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
        Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
        assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
                .containsExactly("Index prefix could not be blank");
    }

    @Test
    void whenScheduleIsNull_thenValidationSucceeds() {
        var crawlerConfig = crawlerConfigBuilder.get().schedule(null).build();
        var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
        Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenScheduleDoesntMatchPattern_thenValidationFails() {
        var crawlerConfig = crawlerConfigBuilder.get().schedule("invalid cron").build();
        var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
        Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
        assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
                .containsExactly("Schedule is not valid");
    }

    @Test
    void whenScheduleMatchesPattern_thenValidationSucceeds() {
        var crawlerConfig = crawlerConfigBuilder.get().schedule(validCron.get()).build();
        var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
        Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenStartUrlsIsNull_thenValidationFails() {
        var crawlerConfig = crawlerConfigBuilder.get().startUrls(null).build();
        var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
        Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
        assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
                .containsExactly("Start-Urls could not be null");
    }

    @Test
    void whenAddedStartUrlIsNull_thenValidationFails() {
        var startUrls = Arrays.asList("https://www.google.com", null);
        var crawlerConfig = crawlerConfigBuilder.get().startUrls(startUrls).build();
        var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
        Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
        assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
                .containsExactly("Start-Url could not be blank");
    }

    @Test
    void whenAddedStartUrlIsEmpty_thenValidationFails() {
        var startUrls = Arrays.asList("https://www.google.com", "");
        var crawlerConfig = crawlerConfigBuilder.get().startUrls(startUrls).build();
        var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
        Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
        assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
                .containsExactly("Start-Url could not be blank");
    }

    @Test
    void whenSiteExclusionPatternsIsNull_thenValidationSucceeds() {
        var crawlerConfig = crawlerConfigBuilder.get().filterOptions(CrawlerFilterOptions.builder().siteExclusionPatterns(null).build()).build();
        var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
        Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenAddedSiteExclusionPatternIsNull_thenValidationFails() {
        var siteExclusionPatterns = Arrays.asList("https://www.google.com", null);
        var crawlerConfig = crawlerConfigBuilder.get().filterOptions(CrawlerFilterOptions.builder().siteExclusionPatterns(siteExclusionPatterns).build()).build();
        var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
        Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
        assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
                .containsExactly("Exclusion-Pattern could not be blank");
    }

    @Test
    void whenAddedSiteExclusionPatternIsEmpty_thenValidationFails() {
        var siteExclusionPatterns = Arrays.asList("https://www.google.com", "");
        var crawlerConfig = crawlerConfigBuilder.get().filterOptions(CrawlerFilterOptions.builder().siteExclusionPatterns(siteExclusionPatterns).build()).build();
        var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
        Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
        assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
                .containsExactly("Exclusion-Pattern could not be blank");
    }

    @Test
    void whenQueryParameterExclusionPatternsIsNull_thenValidationSucceeds() {
        var crawlerConfig = crawlerConfigBuilder.get().filterOptions(CrawlerFilterOptions.builder().queryParameterExclusionPatterns(null).build()).build();
        var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
        Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenAddedQueryParameterExclusionPatternIsNull_thenValidationFails() {
        var queryParameterExclusionPatterns = Arrays.asList("utm_*", null);
        var crawlerConfig = crawlerConfigBuilder.get().filterOptions(CrawlerFilterOptions.builder().queryParameterExclusionPatterns(queryParameterExclusionPatterns).build()).build();
        var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
        Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
        assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
                .containsExactly("Ignored-Query-Parameter could not be blank");
    }

    @Test
    void whenAddedQueryParameterExclusionPatternIsEmpty_thenValidationFails() {
        var queryParameterExclusionPatterns = Arrays.asList("utm_*", "");
        var crawlerConfig = crawlerConfigBuilder.get().filterOptions(CrawlerFilterOptions.builder().queryParameterExclusionPatterns(queryParameterExclusionPatterns).build()).build();
        var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
        Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
        assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
                .containsExactly("Ignored-Query-Parameter could not be blank");
    }

    @Test
    void whenRequestOptionsIsNull_thenValidationFails() {
        var crawlerConfig = crawlerConfigBuilder.get().requestOptions(null).build();
        var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
        Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
        assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
                .containsExactly("Request options could not be null");
    }

    @Test
    void whenRequestOptionProxyIsSet_thenValidationSucceeds() {
        var requestOptions = CrawlerRequestOptions.builder().retries(1).requestTimeout(12).proxy(new ConnectionProxy("localhost", 8080)).build();
        var crawlerConfig = crawlerConfigBuilder.get().requestOptions(requestOptions).build();
        var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
        Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 65536})
    void whenRequestOptionProxyPortIsOutsideOfBoundary_thenValidationFails(int port) {
        var requestOptions = CrawlerRequestOptions.builder().retries(1).requestTimeout(12).proxy(new ConnectionProxy("localhost", port)).build();
        var crawlerConfig = crawlerConfigBuilder.get().requestOptions(requestOptions).build();
        var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
        Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
        assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
                .containsExactly("Port must be between 0 and 65535");
    }

    @Test
    void whenRequestOptionProxyHostIsNull_thenValidationFails() {
        var requestOptions = CrawlerRequestOptions.builder().retries(1).requestTimeout(12).proxy(new ConnectionProxy(null, 8080)).build();
        var crawlerConfig = crawlerConfigBuilder.get().requestOptions(requestOptions).build();
        var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
        Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
        assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
                .containsExactly("Host could not be blank");
    }

    @Test
    void whenRequestOptionProxyHostIsEmpty_thenValidationFails() {
        var requestOptions = CrawlerRequestOptions.builder().requestTimeout(12).retries(1).proxy(new ConnectionProxy("", 8080)).build();
        var crawlerConfig = crawlerConfigBuilder.get().requestOptions(requestOptions).build();
        var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
        Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
        assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
                .containsExactly("Host could not be blank");
    }


    @Test
    void whenRequestOptionRequestTimeoutIsLowerThanZero_thenValidationFails() {
        var requestOptions = CrawlerRequestOptions.builder().requestTimeout(null).retries(1).build();
        var crawlerConfig = crawlerConfigBuilder.get().requestOptions(requestOptions).build();
        var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
        Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
        assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
                .containsExactly("Request timeout could not be null");
    }

    @Test
    void whenRequestOptionRetriesIsNull_thenValidationFails() {
        var requestOptions = CrawlerRequestOptions.builder().requestTimeout(1).retries(null).build();
        var crawlerConfig = crawlerConfigBuilder.get().requestOptions(requestOptions).build();
        var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
        Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
        assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
                .containsExactly("Request retries could not be null");
    }

    @Test
    void whenRequestOptionRequestTimoutIsNull_thenValidationFails() {
        var requestOptions = CrawlerRequestOptions.builder().requestTimeout(null).retries(1).build();
        var crawlerConfig = crawlerConfigBuilder.get().requestOptions(requestOptions).build();
        var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
        Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
        assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
                .containsExactly("Request timeout could not be null");
    }

    @Test
    void whenRequestOptionRetriesIsLowerThanZero_thenValidationFails() {
        var requestOptions = CrawlerRequestOptions.builder().retries(-1).requestTimeout(12).build();
        var crawlerConfig = crawlerConfigBuilder.get().requestOptions(requestOptions).build();
        var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
        Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
        assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
                .containsExactly("Retries must be greater than 0");
    }

    @Test
    void whenRequestOptionHeadersIsNull_thenValidationSucceeds() {
        var requestOptions = CrawlerRequestOptions.builder().headers(null).requestTimeout(12).retries(12).build();
        var crawlerConfig = crawlerConfigBuilder.get().requestOptions(requestOptions).build();
        var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
        Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenAddedRequestOptionHeaderIsNull_thenValidationFails() {
        var headers = Arrays.asList(new ConnectionHeader("name", "type"), null);
        var requestOptions =
                CrawlerRequestOptions.builder().requestTimeout(12).retries(12).headers(headers).build();
        var crawlerConfig = crawlerConfigBuilder.get().requestOptions(requestOptions).build();
        var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
        Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
        assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
                .containsExactly("Request header could not be null");
    }

    @Test
    void whenAddedRequestOptionHeaderNameIsNull_thenValidationFails() {
        var headers = Arrays.asList(new ConnectionHeader(null, "type"));
        var requestOptions =
                CrawlerRequestOptions.builder().requestTimeout(12).retries(12).headers(headers).build();
        var crawlerConfig = crawlerConfigBuilder.get().requestOptions(requestOptions).build();
        var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
        Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);

        assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
                .containsExactly("Request header name could not be null");
    }

    @Test
    void whenAddedRequestOptionHeaderTypeIsNull_thenValidationFails() {
        var headers = Arrays.asList(new ConnectionHeader("name", null));
        var requestOptions =
                CrawlerRequestOptions.builder().requestTimeout(12).retries(12).headers(headers).build();
        var crawlerConfig = crawlerConfigBuilder.get().requestOptions(requestOptions).build();
        var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
        Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);

        assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
                .containsExactly("Request header value could not be null");
    }

    @Test
    void whenRobotOptionsIsNull_thenValidationSucceeds() {
        var crawlerConfig = crawlerConfigBuilder.get().robotOptions(null).build();
        var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
        Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenActionIsNull_thenValidationFails() {
        var crawlerConfig = crawlerConfigBuilder.get().actions(null).build();
        var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
        Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
        assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
                .containsExactly("Actions could not be null");
    }

    @Test
    void whenAddedActionIsNull_thenValidationFails() {
        var actions = Arrays.asList(CrawlerTestDummyProvider.crawlerAction.get(), null);
        var crawlerConfig = crawlerConfigBuilder.get().actions(actions).build();
        var crawlerRequest = new CrawlerRequest("Test Crawler", crawlerConfig);
        Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(crawlerRequest);
        assertThat(violations).isNotEmpty().hasSize(1).extracting(ConstraintViolation::getMessage)
                .containsExactly("Action could not be null");
    }

}
