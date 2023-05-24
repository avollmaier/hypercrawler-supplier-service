package at.hypercrawler.managerservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import at.hypercrawler.managerservice.event.AddressSuppliedMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import at.hypercrawler.managerservice.web.dto.CrawlerAction;
import at.hypercrawler.managerservice.web.dto.CrawlerConfig;
import at.hypercrawler.managerservice.web.dto.CrawlerRequest;
import at.hypercrawler.managerservice.web.dto.CrawlerRequestOptions;
import at.hypercrawler.managerservice.web.dto.CrawlerResponse;
import at.hypercrawler.managerservice.web.dto.CrawlerRobotOptions;
import at.hypercrawler.managerservice.web.dto.CrawlerStatus;
import at.hypercrawler.managerservice.web.dto.Header;
import at.hypercrawler.managerservice.web.dto.SupportedFileType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ImportAutoConfiguration(TestChannelBinderConfiguration.class)
@Testcontainers
class CrawlerMessageTest {

    @Container
    private static final MongoDBContainer mongoContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:latest"));

    Supplier<List<String>> startUrls = () -> Arrays.asList("https://www.google.com", "https://www.bing.com");
    Supplier<List<String>> updatedStartUrls =
            () -> Arrays.asList("https://www.google.com", "https://www.bing.com", "https://www.yahoo.com");

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
            () -> CrawlerConfig.builder().actions(Collections.singletonList(crawlerAction.get()))
                    .indexPrefix("crawler_").requestOptions(crawlerRequestOptions.get()).startUrls(startUrls.get())
                    .schedule("0 0 0 1 1 ? 2099").robotOptions(robotOptions.get())
                    .queryParameterExclusionPatterns(Collections.singletonList("utm_*"))
                    .siteExclusionPatterns(Collections.singletonList("https://www.google.com/**"))
                    .build();
    Supplier<CrawlerRequest> crawlerRequest = () -> new CrawlerRequest("Test Crawler", crawlerConfig.get());

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private OutputDestination output;

    @DynamicPropertySource
    static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoContainer::getReplicaSetUrl);
    }


    @Test
    void whenRunCrawlerRequest_thenCrawlerIsStarted() throws IOException {
        webTestClient.post().uri("/crawlers").contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(crawlerRequest.get())).exchange().expectStatus()
                .isCreated().expectBody(CrawlerResponse.class).returnResult();

        var crawlerResponse = webTestClient.get().uri("/crawlers").exchange().expectStatus().isOk()
                .expectBodyList(CrawlerResponse.class).returnResult().getResponseBody().get(0);

        webTestClient.put().uri("/crawlers/" + crawlerResponse.getId() + "/run").exchange().expectStatus().isOk();


        assertThat(objectMapper.readValue(output.receive().getPayload(), AddressSuppliedMessage.class))
                .isEqualTo(new AddressSuppliedMessage(crawlerResponse.getId(), new URL(crawlerResponse.getConfig().getStartUrls().get(0))));
    }

}
