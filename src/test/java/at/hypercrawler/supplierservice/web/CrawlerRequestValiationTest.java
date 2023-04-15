package at.hypercrawler.supplierservice.web;


import at.hypercrawler.supplierservice.web.dto.CrawlerRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.Set;

public class CrawlerRequestValiationTest {

    private static Validator validator;
    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenAllFieldsCorrectThenValidationSucceeds() {
        var orderRequest = new CrawlerRequest("Test Crawler");
        Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(orderRequest);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenNameIsNullThenValidationFails() {
        var orderRequest = new CrawlerRequest(null);
        Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(orderRequest);
        assertThat(violations).isNotEmpty();
    }

    @Test
    void whenNameIsEmptyThenValidationFails() {
        var orderRequest = new CrawlerRequest("");
        Set<ConstraintViolation<CrawlerRequest>> violations = validator.validate(orderRequest);
        assertThat(violations).isNotEmpty();
    }
}
