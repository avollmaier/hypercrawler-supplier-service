package at.hypercrawler.supplierservice.web;


import at.hypercrawler.supplierservice.web.dto.CrawlerRequest;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
class CrawlerRequestJsonTest {

    @Autowired
    private JacksonTester<CrawlerRequest> json;

    @Test
    void testDeserialize() throws Exception {
        var content = """
                {
                    "name": "Test Crawler"
                }
                """;
        assertThat(this.json.parse(content))
                .usingRecursiveComparison().isEqualTo(new CrawlerRequest("Test Crawler"));
    }

}