package at.hypercrawler.supplierservice;

import at.hypercrawler.supplierservice.domain.CrawlerRepository;
import at.hypercrawler.supplierservice.domain.model.Crawler;
import at.hypercrawler.supplierservice.domain.model.CrawlerStatus;
import at.hypercrawler.supplierservice.web.dto.CrawlerRequest;
import at.hypercrawler.supplierservice.web.dto.CrawlerResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class CrawlerServiceApplicationTest {

    @Container
    private static final MongoDBContainer mongoContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"));

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

        webTestClient.post().uri("/crawlers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(new CrawlerRequest("Test Crawler")))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CrawlerResponse.class)
                .value(crawlerResponse -> {
                    assertThat(crawlerResponse.name()).isEqualTo("Test Crawler");
                    assertThat(crawlerResponse.status()).isEqualTo(CrawlerStatus.CREATED);
                });
    }


    @Test
    void whenGetCrawlerRequest_thenCrawlerIsReturned() throws JsonProcessingException {
        var crawlerResponse = webTestClient.post().uri("/crawlers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(new CrawlerRequest("Test Crawler")))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CrawlerResponse.class)
                .returnResult().getResponseBody();

        webTestClient.get().uri("/crawlers/" + crawlerResponse.id())
                .exchange()
                .expectStatus().isOk()
                .expectBody(CrawlerResponse.class)
                .value(crawlerResponse1 -> {
                    assertThat(crawlerResponse1.id()).isEqualTo(crawlerResponse.id());
                    assertThat(crawlerResponse1.name()).isEqualTo(crawlerResponse.name());
                    assertThat(crawlerResponse1.status()).isEqualTo(crawlerResponse.status());
                });
    }

    @Test
    void whenGetCrawlerRequestWithInvalidId_thenNotFound() {
        webTestClient.get().uri("/crawlers/"+ UUID.randomUUID())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenDeleteCrawlerRequest_thenCrawlerIsDeleted() throws JsonProcessingException {
        var crawlerResponse = webTestClient.post().uri("/crawlers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(new CrawlerRequest("Test Crawler")))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CrawlerResponse.class)
                .returnResult().getResponseBody();

        webTestClient.delete().uri("/crawlers/" + crawlerResponse.id())
                .exchange()
                .expectStatus().isNoContent();

        webTestClient.get().uri("/crawlers/" + crawlerResponse.id())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenDeleteCrawlerRequestWithInvalidId_thenNotFound() throws JsonProcessingException {
        webTestClient.delete().uri("/crawlers/"+ UUID.randomUUID())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void whenRunCrawlerRequest_thenCrawlerStatusIsRunning() throws JsonProcessingException {
        var crawlerResponse = webTestClient.post().uri("/crawlers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(new CrawlerRequest("Test Crawler")))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CrawlerResponse.class)
                .returnResult().getResponseBody();

        webTestClient.post().uri("/crawlers/" + crawlerResponse.id()+ "/run")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CrawlerResponse.class)
                .value(crawlerResponse1 -> {
                    assertThat(crawlerResponse1.id()).isEqualTo(crawlerResponse.id());
                    assertThat(crawlerResponse1.status()).isEqualTo(CrawlerStatus.RUNNING);
                });
    }

    @Test
    void whenPauseCrawlerRequest_thenCrawlerStatusIsPaused() throws JsonProcessingException {
        var crawlerResponse = webTestClient.post().uri("/crawlers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(new CrawlerRequest("Test Crawler")))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CrawlerResponse.class)
                .returnResult().getResponseBody();

        webTestClient.post().uri("/crawlers/" + crawlerResponse.id()+ "/pause")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CrawlerResponse.class)
                .value(crawlerResponse1 -> {
                    assertThat(crawlerResponse1.id()).isEqualTo(crawlerResponse.id());
                    assertThat(crawlerResponse1.status()).isEqualTo(CrawlerStatus.STOPPED);
                });
    }

    @Test
    void whenUpdateCrawlerStatusRequestWithInvalidId_thenNotFound() throws JsonProcessingException {
        webTestClient.post().uri("/crawlers/"+ UUID.randomUUID() + "/run")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void whenGetAllCrawlersRequest_thenAllCrawlersAreReturned() throws JsonProcessingException {
        var crawlerResponse1 = webTestClient.post().uri("/crawlers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(new CrawlerRequest("Test Crawler 1")))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CrawlerResponse.class)
                .returnResult().getResponseBody();

        var crawlerResponse2 = webTestClient.post().uri("/crawlers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(new CrawlerRequest("Test Crawler 2")))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CrawlerResponse.class)
                .returnResult().getResponseBody();

        webTestClient.get().uri("/crawlers")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CrawlerResponse.class);
    }

}
