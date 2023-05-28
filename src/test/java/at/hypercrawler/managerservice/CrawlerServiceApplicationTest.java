package at.hypercrawler.managerservice;

import at.hypercrawler.managerservice.domain.model.CrawlerStatus;
import at.hypercrawler.managerservice.web.dto.CrawlerResponse;
import at.hypercrawler.managerservice.web.dto.StatusResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ImportAutoConfiguration(TestChannelBinderConfiguration.class)
@Testcontainers
class CrawlerServiceApplicationTest {

  @Container
  private static final MongoDBContainer mongoContainer =
    new MongoDBContainer(DockerImageName.parse("mongo:latest"));

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
              .bodyValue(objectMapper.writeValueAsString(CrawlerTestDummyProvider.crawlerRequest.get())).exchange().expectStatus().isCreated()
              .expectBody(CrawlerResponse.class).value(actualCrawlerResponse -> {
                  assertThat(actualCrawlerResponse.name()).isEqualTo("Test Crawler");
                  assertThat(actualCrawlerResponse.status()).isEqualTo(CrawlerStatus.CREATED);
              });
    }

    @Test
    void whenGetAllCrawlersRequest_thenAllCrawlersAreReturned() throws JsonProcessingException {
        webTestClient.post().uri("/crawlers").contentType(MediaType.APPLICATION_JSON).bodyValue(objectMapper.writeValueAsString(CrawlerTestDummyProvider.crawlerRequest.get())).exchange().expectStatus().isCreated().expectBody(CrawlerResponse.class).returnResult();
        webTestClient.post().uri("/crawlers").contentType(MediaType.APPLICATION_JSON).bodyValue(objectMapper.writeValueAsString(CrawlerTestDummyProvider.crawlerRequest.get())).exchange().expectStatus().isCreated().expectBody(CrawlerResponse.class).returnResult();

        webTestClient.get().uri("/crawlers").exchange().expectStatus().isOk().expectBodyList(CrawlerResponse.class);
    }

    @Test
    void whenGetCrawlerRequest_thenCrawlerIsReturned() throws JsonProcessingException {
        var crawlerResponse = webTestClient.post().uri("/crawlers").contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(CrawlerTestDummyProvider.crawlerRequest.get())).exchange().expectStatus()
                .isCreated().expectBody(CrawlerResponse.class).returnResult().getResponseBody();
        assertNotNull(crawlerResponse);

        webTestClient.get().uri("/crawlers/" + crawlerResponse.id()).exchange().expectStatus().isOk()
                .expectBody(CrawlerResponse.class).value(actualResponse -> {
                    assertThat(actualResponse.id()).isEqualTo(crawlerResponse.id());
                    assertThat(actualResponse.name()).isEqualTo(crawlerResponse.name());
                    assertThat(actualResponse.status()).isEqualTo(crawlerResponse.status());
                });
    }

    @Test
    void whenGetCrawlerStatusRequest_thenCrawlerStatusIsReturned() throws JsonProcessingException {
        var crawlerResponse = webTestClient.post().uri("/crawlers").contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(CrawlerTestDummyProvider.crawlerRequest.get())).exchange().expectStatus()
                .isCreated().expectBody(CrawlerResponse.class).returnResult().getResponseBody();
        assertNotNull(crawlerResponse);

        webTestClient.get().uri("/crawlers/" + crawlerResponse.id() + "/status").exchange().expectStatus().isOk()
                .expectBody(StatusResponse.class).value(actualCrawlerResponse -> {
                    assertThat(crawlerResponse.status()).isEqualTo(actualCrawlerResponse.status());
                });
    }

    @Test
    void whenGetCrawlerRequestWithInvalidId_thenNotFound() {
        webTestClient.get().uri("/crawlers/" + UUID.randomUUID()).exchange().expectStatus().isNotFound();
    }

    @Test
    void whenDeleteCrawlerRequest_thenCrawlerIsDeleted() throws JsonProcessingException {
      var crawlerResponse = webTestClient.post().uri("/crawlers").contentType(MediaType.APPLICATION_JSON)
              .bodyValue(objectMapper.writeValueAsString(CrawlerTestDummyProvider.crawlerRequest.get())).exchange().expectStatus()
        .isCreated().expectBody(CrawlerResponse.class).returnResult().getResponseBody();

      assertNotNull(crawlerResponse);

        webTestClient.delete().uri("/crawlers/" + crawlerResponse.id()).exchange().expectStatus()
                .isNoContent();
        webTestClient.get().uri("/crawlers/" + crawlerResponse.id()).exchange().expectStatus().isNotFound();
    }

    @Test
    void whenDeleteCrawlerRequestWithInvalidId_thenNotFound() {
        webTestClient.delete().uri("/crawlers/" + UUID.randomUUID()).exchange().expectStatus().isNoContent();
    }

    @Test
    void whenUpdateCrawlerRequest_thenCrawlerIsUpdated() throws JsonProcessingException {

        webTestClient.post().uri("/crawlers").contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(CrawlerTestDummyProvider.crawlerRequest.get())).exchange().expectStatus()
                .isCreated().expectBody(CrawlerResponse.class).returnResult();
        var crawlerResponse = webTestClient.get().uri("/crawlers").exchange().expectStatus().isOk()
                .expectBodyList(CrawlerResponse.class).returnResult().getResponseBody().get(0);

        webTestClient.put().uri("/crawlers/" + crawlerResponse.id()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(CrawlerTestDummyProvider.updatedCrawlerRequest.get())).exchange().expectStatus()
                .isOk().expectBody(CrawlerResponse.class).value(c -> {
                    assertThat(c.id()).isEqualTo(crawlerResponse.id());
                    assertThat(c.name()).isEqualTo(CrawlerTestDummyProvider.updatedCrawlerRequest.get().name());
                    assertThat(c.status()).isEqualTo(crawlerResponse.status());

                    // assert that config is updated
                    assertThat(c.createdAt()).isEqualTo(crawlerResponse.createdAt());
                    assertThat(c.updatedAt()).isAfter(crawlerResponse.updatedAt());
                });
    }

    @Test
    void whenRunCrawlerRequest_thenCrawlerIsStarted() throws IOException {
        webTestClient.post().uri("/crawlers").contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(CrawlerTestDummyProvider.crawlerRequest.get())).exchange().expectStatus()
        .isCreated().expectBody(CrawlerResponse.class).returnResult();

      var crawlerResponse = webTestClient.get().uri("/crawlers").exchange().expectStatus().isOk()
        .expectBodyList(CrawlerResponse.class).returnResult().getResponseBody().get(0);

        webTestClient.put().uri("/crawlers/" + crawlerResponse.id() + "/run").exchange().expectStatus()
        .isOk().expectBody(CrawlerResponse.class).value(c -> {
                    assertThat(c.id()).isEqualTo(crawlerResponse.id());
                    assertThat(c.name()).isEqualTo(crawlerResponse.name());
                    assertThat(c.status()).isEqualTo(CrawlerStatus.STARTED);
                    assertThat(c.config()).isEqualTo(crawlerResponse.config());
                    assertThat(c.createdAt()).isEqualTo(crawlerResponse.createdAt());
                    assertThat(c.updatedAt()).isAfter(crawlerResponse.updatedAt());
                });
    }

    @Test
    void whenStopCrawlerRequest_thenCrawlerIsStopped() throws IOException {
        webTestClient.post().uri("/crawlers").contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(CrawlerTestDummyProvider.crawlerRequest.get())).exchange().expectStatus()
        .isCreated().expectBody(CrawlerResponse.class).returnResult();

      var crawlerResponse = webTestClient.get().uri("/crawlers").exchange().expectStatus().isOk()
        .expectBodyList(CrawlerResponse.class).returnResult().getResponseBody().get(0);

        webTestClient.put().uri("/crawlers/" + crawlerResponse.id() + "/pause").exchange().expectStatus()
        .isOk().expectBody(CrawlerResponse.class).value(c -> {
                    assertThat(c.id()).isEqualTo(crawlerResponse.id());
                    assertThat(c.name()).isEqualTo(crawlerResponse.name());
                    assertThat(c.status()).isEqualTo(CrawlerStatus.STOPPED);
                    assertThat(c.config()).isEqualTo(crawlerResponse.config());
                    assertThat(c.createdAt()).isEqualTo(crawlerResponse.createdAt());
                    assertThat(c.updatedAt()).isAfter(crawlerResponse.updatedAt());
                });

      assertNull(output.receive());
    }

    @Test
    void whenUpdateCrawlerRequestWithInvalidId_thenNotFound() throws JsonProcessingException {
        webTestClient.put().uri("/crawlers/" + UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON).bodyValue(objectMapper.writeValueAsString(CrawlerTestDummyProvider.updatedCrawlerRequest.get())).exchange().expectStatus().isNotFound();
    }

}
