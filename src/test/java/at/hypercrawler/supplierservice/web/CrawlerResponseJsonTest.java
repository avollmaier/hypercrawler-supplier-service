package at.hypercrawler.supplierservice.web;

import at.hypercrawler.supplierservice.domain.model.CrawlerConfig;
import at.hypercrawler.supplierservice.domain.model.CrawlerStatus;
import at.hypercrawler.supplierservice.domain.model.SupportedFileType;
import at.hypercrawler.supplierservice.web.dto.CrawlerRequest;
import at.hypercrawler.supplierservice.web.dto.CrawlerResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CrawlerResponseJsonTest {

    @Autowired
    private JacksonTester<CrawlerResponse> json;

    Supplier<List<String>> startUrls = () -> Arrays.asList("https://www.google.com", "https://www.bing.com");
    Supplier<List<SupportedFileType>> fileTypesToMatch = () -> Arrays.asList(SupportedFileType.HTML, SupportedFileType.PDF);
    Supplier<List<String>> pathsToMatch = () -> Arrays.asList("http://www.foufos.gr/**");
    Supplier<List<String>> selectorsToMatch = () -> Arrays.asList(".products", "!.featured");
    Supplier<CrawlerConfig> crawlerConfig = () -> new CrawlerConfig(startUrls.get(), fileTypesToMatch.get(), pathsToMatch.get(), selectorsToMatch.get());
    Supplier<CrawlerRequest> crawlerRequest = () -> new CrawlerRequest("Test Crawler", crawlerConfig.get());


    @Test
    void testSerialize()
            throws Exception {
        var crawler = new CrawlerResponse(UUID.randomUUID(), "Test Crawler", CrawlerStatus.CREATED, crawlerConfig.get(), Instant.now(),
                Instant.now());
        var jsonContent = json.write(crawler);
        assertThat(jsonContent).extractingJsonPathStringValue("@.id").isEqualTo(crawler.id().toString());
        assertThat(jsonContent).extractingJsonPathStringValue("@.name").isEqualTo(crawler.name());
        assertThat(jsonContent).extractingJsonPathStringValue("@.status").isEqualTo(crawler.status().toString());
        assertThat(jsonContent).extractingJsonPathStringValue("@.config.startUrls[0]")
                .isEqualTo(crawler.config().startUrls().get(0));
        assertThat(jsonContent).extractingJsonPathStringValue("@.config.startUrls[1]").isEqualTo(crawler.config().startUrls().get(1));
        assertThat(jsonContent).extractingJsonPathStringValue("@.config.fileTypesToMatch[0]")
                .isEqualTo(crawler.config().fileTypesToMatch().get(0).toString());
        assertThat(jsonContent).extractingJsonPathStringValue("@.config.fileTypesToMatch[1]")
                .isEqualTo(crawler.config().fileTypesToMatch().get(1).toString());
        assertThat(jsonContent).extractingJsonPathStringValue("@.createdAt")
                .isEqualTo(crawler.createdAt().toString());
        assertThat(jsonContent).extractingJsonPathStringValue("@.updatedAt")
                .isEqualTo(crawler.updatedAt().toString());
    }

}
