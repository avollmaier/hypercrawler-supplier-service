package at.hypercrawler.managerservice.event;

import at.hypercrawler.managerservice.CrawlerTestDummyProvider;
import at.hypercrawler.managerservice.web.dto.CrawlerResponse;
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
import java.net.URL;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ImportAutoConfiguration(TestChannelBinderConfiguration.class)
@Testcontainers
class CrawlerManagerMessageTest {

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
    void whenRunCrawlerRequest_thenCrawlerIsStarted() throws IOException {
        webTestClient.post().uri("/crawlers").contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(CrawlerTestDummyProvider.crawlerRequest.get())).exchange().expectStatus()
                .isCreated().expectBody(CrawlerResponse.class).returnResult();

        var crawlerResponse = Objects.requireNonNull(webTestClient.get().uri("/crawlers").exchange().expectStatus().isOk()
                .expectBodyList(CrawlerResponse.class).returnResult().getResponseBody()).get(0);

        webTestClient.put().uri("/crawlers/" + crawlerResponse.id() + "/run").exchange().expectStatus().isOk();


        assertThat(objectMapper.readValue(output.receive().getPayload(), AddressSuppliedMessage.class))
                .isEqualTo(new AddressSuppliedMessage(crawlerResponse.id(), List.of(new URL(crawlerResponse.config().startUrls().get(0)), new URL(crawlerResponse.config().startUrls().get(1)))));
    }

}
