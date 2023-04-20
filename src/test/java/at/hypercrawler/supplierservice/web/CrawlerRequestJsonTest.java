package at.hypercrawler.supplierservice.web;

import at.hypercrawler.supplierservice.domain.model.CrawlerConfig;
import at.hypercrawler.supplierservice.domain.model.SupportedFileType;
import at.hypercrawler.supplierservice.web.dto.CrawlerRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CrawlerRequestJsonTest {

    @Autowired
    private JacksonTester<CrawlerRequest> json;

    Supplier<List<String>> startUrls = () -> Arrays.asList("https://www.google.com", "https://www.bing.com");
    Supplier<List<SupportedFileType>> fileTypesToMatch = () -> Arrays.asList(SupportedFileType.HTML, SupportedFileType.PDF);
    Supplier<List<String>> pathsToMatch = () -> Arrays.asList("http://www.foufos.gr/**");
    Supplier<List<String>> selectorsToMatch = () -> Arrays.asList(".products", "!.featured");
    Supplier<CrawlerConfig> crawlerConfig = () -> new CrawlerConfig(startUrls.get(), fileTypesToMatch.get(), pathsToMatch.get(), selectorsToMatch.get());
    Supplier<CrawlerRequest> crawlerRequest = () -> new CrawlerRequest("Test Crawler", crawlerConfig.get());


    @Test
    void testDeserialize()
            throws Exception {
        var content = """
                {
                    "name": "Test Crawler",
                    "config": {
                        "startUrls": [
                            "https://www.google.com",
                            "https://www.bing.com"
                        ],
                        "pathsToMatch": [
                            "http://www.foufos.gr/**"
                        ],
                        "fileTypesToMatch": [
                            "HTML",
                            "PDF"
                        ],
                        "selectorsToMatch": [
                            ".products",
                            "!.featured"
                        ]
                    }
                }
                """;
        assertThat(this.json.parse(content)).usingRecursiveComparison()
                .isEqualTo(crawlerRequest.get());
    }

}