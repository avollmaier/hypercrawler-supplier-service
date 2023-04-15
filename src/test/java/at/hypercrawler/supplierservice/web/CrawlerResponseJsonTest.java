package at.hypercrawler.supplierservice.web;

import at.hypercrawler.supplierservice.domain.model.Crawler;
import at.hypercrawler.supplierservice.domain.model.CrawlerStatus;
import at.hypercrawler.supplierservice.web.dto.CrawlerResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CrawlerResponseJsonTest {

    @Autowired
    private JacksonTester<CrawlerResponse> json;

    @Test
    void testSerialize() throws Exception {
        var crawler = new CrawlerResponse(UUID.randomUUID(), "Test Crawler", CrawlerStatus.CREATED, Instant.now(), Instant.now());
        var jsonContent = json.write(crawler);
        assertThat(jsonContent).extractingJsonPathStringValue("@.id").isEqualTo(crawler.id().toString());
        assertThat(jsonContent).extractingJsonPathStringValue("@.name").isEqualTo(crawler.name());
        assertThat(jsonContent).extractingJsonPathStringValue("@.status").isEqualTo(crawler.status().toString());
        assertThat(jsonContent).extractingJsonPathStringValue("@.createdAt").isEqualTo(crawler.createdAt().toString());
        assertThat(jsonContent).extractingJsonPathStringValue("@.updatedAt").isEqualTo(crawler.updatedAt().toString());
    }

}
