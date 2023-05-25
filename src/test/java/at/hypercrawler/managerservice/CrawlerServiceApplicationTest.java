package at.hypercrawler.managerservice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import at.hypercrawler.managerservice.web.dto.*;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ImportAutoConfiguration(TestChannelBinderConfiguration.class)
@Testcontainers
class CrawlerServiceApplicationTest {

  @Container
  private static final MongoDBContainer mongoContainer =
    new MongoDBContainer(DockerImageName.parse("mongo:latest"));

  Supplier<List<String>> startUrls = () -> Arrays.asList("https://www.google.com", "https://www.bing.com");
  Supplier<List<String>> updatedStartUrls =
    () -> Arrays.asList("https://www.google.com", "https://www.bing.com", "https://www.yahoo.com");

  Supplier<List<SupportedFileType>> fileTypesToMatch =
    () -> Arrays.asList(SupportedFileType.HTML, SupportedFileType.PDF);

  Supplier<List<SupportedFileType>> updatedFileTypesToMatch =
    () -> Arrays.asList(SupportedFileType.HTML, SupportedFileType.PDF, SupportedFileType.TXT);

  Supplier<List<String>> pathsToMatch = () -> List.of("http://www.foufos.gr/**");
  Supplier<List<String>> updatedPathsToMatch =
    () -> List.of("http://www.foufos.gr/**", "http://www.foufos.gr/**");

  Supplier<List<String>> selectorsToMatch = () -> Arrays.asList(".products", "!.featured");
  Supplier<List<String>> updatedSelectorsToMatch =
    () -> Arrays.asList(".noproducts", "!.featured", ".feature");

  Supplier<CrawlerAction> crawlerAction =
    () -> CrawlerAction.builder().fileTypesToMatch(fileTypesToMatch.get()).pathsToMatch(pathsToMatch.get())
      .selectorsToMatch(selectorsToMatch.get()).indexName("test_index").build();
  Supplier<CrawlerAction> updatedCrawlerAction =
    () -> CrawlerAction.builder().fileTypesToMatch(updatedFileTypesToMatch.get())
      .pathsToMatch(updatedPathsToMatch.get()).selectorsToMatch(updatedSelectorsToMatch.get())
      .indexName("test2_index").build();
  Supplier<CrawlerRequestOptions> crawlerRequestOptions =
    () -> CrawlerRequestOptions.builder().requestTimeout(1000).proxy("http://localhost:8080").retries(3)
      .headers(Collections.singletonList(new Header("User-Agent", "Mozilla/5.0 (compatible"))).build();
  Supplier<CrawlerRequestOptions> updatedCrawlerRequestOptions =
    () -> CrawlerRequestOptions.builder().requestTimeout(11000).proxy("http://localhost:8090").retries(32)
      .headers(Collections.singletonList(new Header("User-Agent", "Chrome/5.0 (compatible"))).build();
  Supplier<CrawlerRobotOptions> robotOptions =
    () -> CrawlerRobotOptions.builder().ignoreRobotNoFollowTo(true).ignoreRobotRules(true)
      .ignoreRobotNoIndex(true).build();
  Supplier<CrawlerConfig> crawlerConfig =
    () -> CrawlerConfig.builder().actions(Collections.singletonList(crawlerAction.get()))
      .indexPrefix("crawler_").requestOptions(crawlerRequestOptions.get()).startUrls(startUrls.get())
      .schedule("0 0 0 1 1 ? 2099").robotOptions(robotOptions.get())
      .queryParameterExclusionPatterns(Collections.singletonList("utm_*"))
      .siteExclusionPatterns(Collections.singletonList("https://www.google.com/**")).build();
  Supplier<CrawlerRequest> crawlerRequest = () -> new CrawlerRequest("Test Crawler", crawlerConfig.get());
  Supplier<CrawlerRobotOptions> updatedRobotOptions =
    () -> CrawlerRobotOptions.builder().ignoreRobotNoFollowTo(false).ignoreRobotRules(true)
      .ignoreRobotNoIndex(false).build();
  Supplier<CrawlerConfig> updatedCrawlerConfig =
    () -> CrawlerConfig.builder().actions(Collections.singletonList(updatedCrawlerAction.get()))
      .indexPrefix("crawlerr_").requestOptions(updatedCrawlerRequestOptions.get())
      .startUrls(updatedStartUrls.get()).schedule("0 0 2 1 1 ? 2099").robotOptions(updatedRobotOptions.get())
      .queryParameterExclusionPatterns(Collections.singletonList("utc_*"))
      .siteExclusionPatterns(Collections.singletonList("https://www.yahoo.com/**")).build();
  Supplier<CrawlerRequest> updatedCrawlerRequest =
    () -> new CrawlerRequest("Updated Test Crawler", updatedCrawlerConfig.get());
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
  void whenPostCrawlerRequest_thenCrawlerIsCreated()
    throws JsonProcessingException {

    webTestClient.post().uri("/crawlers").contentType(MediaType.APPLICATION_JSON)
      .bodyValue(objectMapper.writeValueAsString(crawlerRequest.get())).exchange().expectStatus().isCreated()
      .expectBody(CrawlerResponse.class).value(crawlerResponse -> {
        assertThat(crawlerResponse.getName()).isEqualTo("Test Crawler");
        assertThat(crawlerResponse.getStatus()).isEqualTo(CrawlerStatus.CREATED);
      });
    }

    @Test
    void whenGetAllCrawlersRequest_thenAllCrawlersAreReturned() throws JsonProcessingException {
        webTestClient.post().uri("/crawlers").contentType(MediaType.APPLICATION_JSON).bodyValue(objectMapper.writeValueAsString(crawlerRequest.get())).exchange().expectStatus().isCreated().expectBody(CrawlerResponse.class).returnResult();
        webTestClient.post().uri("/crawlers").contentType(MediaType.APPLICATION_JSON).bodyValue(objectMapper.writeValueAsString(crawlerRequest.get())).exchange().expectStatus().isCreated().expectBody(CrawlerResponse.class).returnResult();

        webTestClient.get().uri("/crawlers").exchange().expectStatus().isOk().expectBodyList(CrawlerResponse.class);
    }

    @Test
    void whenGetCrawlerRequest_thenCrawlerIsReturned() throws JsonProcessingException {
      var crawlerResponse = webTestClient.post().uri("/crawlers").contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(crawlerRequest.get())).exchange().expectStatus()
        .isCreated().expectBody(CrawlerResponse.class).returnResult().getResponseBody();
      assertNotNull(crawlerResponse);

      webTestClient.get().uri("/crawlers/" + crawlerResponse.getId()).exchange().expectStatus().isOk()
        .expectBody(CrawlerResponse.class).value(crawlerResponse1 -> {
          assertThat(crawlerResponse1.getId()).isEqualTo(crawlerResponse.getId());
          assertThat(crawlerResponse1.getName()).isEqualTo(crawlerResponse.getName());
          assertThat(crawlerResponse1.getStatus()).isEqualTo(crawlerResponse.getStatus());
        });
    }

    @Test
    void whenGetCrawlerStatusRequest_thenCrawlerStatusIsReturned() throws JsonProcessingException {
        var crawlerResponse = webTestClient.post().uri("/crawlers").contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(crawlerRequest.get())).exchange().expectStatus()
                .isCreated().expectBody(CrawlerResponse.class).returnResult().getResponseBody();
        assertNotNull(crawlerResponse);

        webTestClient.get().uri("/crawlers/" + crawlerResponse.getId() + "/status").exchange().expectStatus().isOk()
                .expectBody(StatusResponse.class).value(crawlerResponse1 -> {
                    assertThat(crawlerResponse.getStatus()).isEqualTo(crawlerResponse.getStatus());
                });
    }


    @Test
    void whenGetCrawlerRequestWithInvalidId_thenNotFound() {
        webTestClient.get().uri("/crawlers/" + UUID.randomUUID()).exchange().expectStatus().isNotFound();
    }

    @Test
    void whenDeleteCrawlerRequest_thenCrawlerIsDeleted() throws JsonProcessingException {
      var crawlerResponse = webTestClient.post().uri("/crawlers").contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(crawlerRequest.get())).exchange().expectStatus()
        .isCreated().expectBody(CrawlerResponse.class).returnResult().getResponseBody();

      assertNotNull(crawlerResponse);

      webTestClient.delete().uri("/crawlers/" + crawlerResponse.getId()).exchange().expectStatus()
        .isNoContent();
      webTestClient.get().uri("/crawlers/" + crawlerResponse.getId()).exchange().expectStatus().isNotFound();
    }

    @Test
    void whenDeleteCrawlerRequestWithInvalidId_thenNotFound() {
        webTestClient.delete().uri("/crawlers/" + UUID.randomUUID()).exchange().expectStatus().isNoContent();
    }

    @Test
    void whenUpdateCrawlerRequest_thenCrawlerIsUpdated() throws JsonProcessingException {

      webTestClient.post().uri("/crawlers").contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(crawlerRequest.get())).exchange().expectStatus()
        .isCreated().expectBody(CrawlerResponse.class).returnResult();
      var crawlerResponse = webTestClient.get().uri("/crawlers").exchange().expectStatus().isOk()
        .expectBodyList(CrawlerResponse.class).returnResult().getResponseBody().get(0);

      webTestClient.put().uri("/crawlers/" + crawlerResponse.getId()).contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(updatedCrawlerRequest.get())).exchange().expectStatus()
        .isOk().expectBody(CrawlerResponse.class).value(c -> {
          assertThat(c.getId()).isEqualTo(crawlerResponse.getId());
          assertThat(c.getName()).isEqualTo(updatedCrawlerRequest.get().getName());
          assertThat(c.getStatus()).isEqualTo(crawlerResponse.getStatus());

          // assert that config is updated
          assertThat(c.getCreatedAt()).isEqualTo(crawlerResponse.getCreatedAt());
          assertThat(c.getUpdatedAt()).isAfter(crawlerResponse.getUpdatedAt());
        });
    }

    @Test
    void whenRunCrawlerRequest_thenCrawlerIsStarted() throws IOException {
      webTestClient.post().uri("/crawlers").contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(crawlerRequest.get())).exchange().expectStatus()
        .isCreated().expectBody(CrawlerResponse.class).returnResult();

      var crawlerResponse = webTestClient.get().uri("/crawlers").exchange().expectStatus().isOk()
        .expectBodyList(CrawlerResponse.class).returnResult().getResponseBody().get(0);

      webTestClient.put().uri("/crawlers/" + crawlerResponse.getId() + "/run").exchange().expectStatus()
        .isOk().expectBody(CrawlerResponse.class).value(c -> {
          assertThat(c.getId()).isEqualTo(crawlerResponse.getId());
          assertThat(c.getName()).isEqualTo(crawlerResponse.getName());
          assertThat(c.getStatus()).isEqualTo(CrawlerStatus.STARTED);
          assertThat(c.getConfig()).isEqualTo(crawlerResponse.getConfig());
          assertThat(c.getCreatedAt()).isEqualTo(crawlerResponse.getCreatedAt());
          assertThat(c.getUpdatedAt()).isAfter(crawlerResponse.getUpdatedAt());
        });
    }

    @Test
    void whenStopCrawlerRequest_thenCrawlerIsStopped() throws IOException {
      webTestClient.post().uri("/crawlers").contentType(MediaType.APPLICATION_JSON)
        .bodyValue(objectMapper.writeValueAsString(crawlerRequest.get())).exchange().expectStatus()
        .isCreated().expectBody(CrawlerResponse.class).returnResult();

      var crawlerResponse = webTestClient.get().uri("/crawlers").exchange().expectStatus().isOk()
        .expectBodyList(CrawlerResponse.class).returnResult().getResponseBody().get(0);

      webTestClient.put().uri("/crawlers/" + crawlerResponse.getId() + "/pause").exchange().expectStatus()
        .isOk().expectBody(CrawlerResponse.class).value(c -> {
          assertThat(c.getId()).isEqualTo(crawlerResponse.getId());
          assertThat(c.getName()).isEqualTo(crawlerResponse.getName());
          assertThat(c.getStatus()).isEqualTo(CrawlerStatus.STOPPED);
          assertThat(c.getConfig()).isEqualTo(crawlerResponse.getConfig());
          assertThat(c.getCreatedAt()).isEqualTo(crawlerResponse.getCreatedAt());
          assertThat(c.getUpdatedAt()).isAfter(crawlerResponse.getUpdatedAt());
        });

      assertNull(output.receive());
    }

    @Test
    void whenUpdateCrawlerRequestWithInvalidId_thenNotFound() throws JsonProcessingException {
        webTestClient.put().uri("/crawlers/" + UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON).bodyValue(objectMapper.writeValueAsString(updatedCrawlerRequest.get())).exchange().expectStatus().isNotFound();
    }

}
