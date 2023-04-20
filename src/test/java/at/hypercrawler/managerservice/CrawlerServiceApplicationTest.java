package at.hypercrawler.managerservice;

import at.hypercrawler.managerservice.domain.model.CrawlerConfig;
import at.hypercrawler.managerservice.domain.model.CrawlerStatus;
import at.hypercrawler.managerservice.domain.model.SupportedFileType;
import at.hypercrawler.managerservice.web.dto.CrawlerRequest;
import at.hypercrawler.managerservice.web.dto.CrawlerResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class CrawlerServiceApplicationTest {

    @Container
    private static final MongoDBContainer mongoContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"));
    Supplier<List<String>> startUrls = () -> Arrays.asList("https://www.google.com", "https://www.bing.com");
    Supplier<List<SupportedFileType>> fileTypesToMatch = () -> Arrays.asList(SupportedFileType.HTML, SupportedFileType.PDF);
    Supplier<List<String>> pathsToMatch = () -> Arrays.asList("http://www.foufos.gr/**", "http://www.foufos");
    Supplier<List<String>> selectorsToMatch = () -> Arrays.asList(".products", "!.featured");
    Supplier<CrawlerConfig> crawlerConfig = () -> new CrawlerConfig(startUrls.get(), fileTypesToMatch.get(), pathsToMatch.get(), selectorsToMatch.get());
    Supplier<CrawlerRequest> crawlerRequest = () -> new CrawlerRequest("Test Crawler", crawlerConfig.get());
    Supplier<List<String>> updatedStartUrls = () -> List.of("https://www.bing.com");
    Supplier<List<SupportedFileType>> updatedFileTypesToMatch = () -> List.of(SupportedFileType.HTML);
    Supplier<List<String>> updatedPathsToMatch = () -> List.of("http://www.foufos.gr/**");
    Supplier<List<String>> updatedSelectorsToMatch = () -> Arrays.asList(".products", "!.featured");
    Supplier<CrawlerConfig> updatedCrawlerConfig = () -> new CrawlerConfig(updatedStartUrls.get(), updatedFileTypesToMatch.get(), updatedPathsToMatch.get(), updatedSelectorsToMatch.get());
    Supplier<CrawlerRequest> updatedCrawlerRequest = () -> new CrawlerRequest("Updated Crawler", updatedCrawlerConfig.get());
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebTestClient webTestClient;

    @DynamicPropertySource
    static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoContainer::getReplicaSetUrl);
    }

    @Test
    void whenPostCrawlerRequest_thenCrawlerIsCreated() throws JsonProcessingException {

        webTestClient.post().uri("/crawlers").contentType(MediaType.APPLICATION_JSON).bodyValue(objectMapper.writeValueAsString(crawlerRequest.get())).exchange().expectStatus().isCreated().expectBody(CrawlerResponse.class).value(crawlerResponse -> {
            assertThat(crawlerResponse.name()).isEqualTo("Test Crawler");
            assertThat(crawlerResponse.status()).isEqualTo(CrawlerStatus.CREATED);
        });
    }

    @Test
    void whenGetAllCrawlersRequest_thenAllCrawlersAreReturned() throws JsonProcessingException {
        var crawlerResponse1 = webTestClient.post().uri("/crawlers").contentType(MediaType.APPLICATION_JSON).bodyValue(objectMapper.writeValueAsString(crawlerRequest.get())).exchange().expectStatus().isCreated().expectBody(CrawlerResponse.class).returnResult().getResponseBody();

        var crawlerResponse2 = webTestClient.post().uri("/crawlers").contentType(MediaType.APPLICATION_JSON).bodyValue(objectMapper.writeValueAsString(crawlerRequest.get())).exchange().expectStatus().isCreated().expectBody(CrawlerResponse.class).returnResult().getResponseBody();

        webTestClient.get().uri("/crawlers").exchange().expectStatus().isOk().expectBodyList(CrawlerResponse.class);
    }

    @Test
    void whenGetCrawlerRequest_thenCrawlerIsReturned() throws JsonProcessingException {
        var crawlerResponse = webTestClient.post().uri("/crawlers").contentType(MediaType.APPLICATION_JSON).bodyValue(objectMapper.writeValueAsString(crawlerRequest.get())).exchange().expectStatus().isCreated().expectBody(CrawlerResponse.class).returnResult().getResponseBody();

        webTestClient.get().uri("/crawlers/" + crawlerResponse.id()).exchange().expectStatus().isOk().expectBody(CrawlerResponse.class).value(crawlerResponse1 -> {
            assertThat(crawlerResponse1.id()).isEqualTo(crawlerResponse.id());
            assertThat(crawlerResponse1.name()).isEqualTo(crawlerResponse.name());
            assertThat(crawlerResponse1.status()).isEqualTo(crawlerResponse.status());
        });
    }

    @Test
    void whenGetCrawlerRequestWithInvalidId_thenNotFound() {
        webTestClient.get().uri("/crawlers/" + UUID.randomUUID()).exchange().expectStatus().isNotFound();
    }

    @Test
    void whenDeleteCrawlerRequest_thenCrawlerIsDeleted() throws JsonProcessingException {
        var crawlerResponse = webTestClient.post().uri("/crawlers").contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(crawlerRequest.get())).exchange().expectStatus().isCreated().expectBody(CrawlerResponse.class).returnResult().getResponseBody();

        webTestClient.delete().uri("/crawlers/" + crawlerResponse.id()).exchange().expectStatus().isNoContent();

        webTestClient.get().uri("/crawlers/" + crawlerResponse.id()).exchange().expectStatus().isNotFound();
    }

    @Test
    void whenDeleteCrawlerRequestWithInvalidId_thenNotFound() throws JsonProcessingException {
        webTestClient.delete().uri("/crawlers/" + UUID.randomUUID()).exchange().expectStatus().isNoContent();
    }

    @Test
    void whenUpdateCrawlerStatusRequest_thenCrawlerStatusIsUpdated() throws JsonProcessingException {

        webTestClient.post().uri("/crawlers").contentType(MediaType.APPLICATION_JSON).bodyValue(objectMapper.writeValueAsString(crawlerRequest.get())).exchange().expectStatus().isCreated().expectBody(CrawlerResponse.class).returnResult().getResponseBody();

        var crawlerResponse = webTestClient.get().uri("/crawlers").exchange().expectStatus().isOk().expectBodyList(CrawlerResponse.class).returnResult().getResponseBody().get(0);

        webTestClient.put().uri("/crawlers/" + crawlerResponse.id()).contentType(MediaType.APPLICATION_JSON).bodyValue(objectMapper.writeValueAsString(updatedCrawlerRequest.get())).exchange().expectStatus().isOk().expectBody(CrawlerResponse.class).value(c -> {
            assertThat(c.id()).isEqualTo(crawlerResponse.id());
            assertThat(c.name()).isEqualTo(updatedCrawlerRequest.get().name());
            assertThat(c.status()).isEqualTo(crawlerResponse.status());
            assertThat(c.config().startUrls()).isEqualTo(updatedCrawlerRequest.get().config().startUrls());
            assertThat(c.config().fileTypesToMatch()).isEqualTo(updatedCrawlerRequest.get().config().fileTypesToMatch());
            assertThat(c.createdAt()).isEqualTo(crawlerResponse.createdAt());
            assertThat(c.updatedAt()).isAfter(crawlerResponse.updatedAt());
        });
    }

    @Test
    void whenUpdateCrawlerStatusRequestWithInvalidId_thenNotFound() throws JsonProcessingException {
        webTestClient.put().uri("/crawlers/" + UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON).bodyValue(objectMapper.writeValueAsString(updatedCrawlerRequest.get())).exchange().expectStatus().isNotFound();
    }

}
