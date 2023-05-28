package at.hypercrawler.managerservice.domain;

import at.hypercrawler.managerservice.CrawlerTestDummyProvider;
import at.hypercrawler.managerservice.domain.model.CrawlerStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.test.StepVerifier;

import java.util.UUID;

@DataMongoTest
@Testcontainers
class CrawlerManagerRepositoryTest {
    @Container
    private static final MongoDBContainer mongoContainer =
      new MongoDBContainer(DockerImageName.parse("mongo:latest"));

    @Autowired
    private CrawlerManagerRepository crawlerManagerRepository;

    @DynamicPropertySource
    static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoContainer::getReplicaSetUrl);
    }

    @Test
    void findCrawlerByIdWhenNotExisting() {
        StepVerifier.create(crawlerManagerRepository.findById(UUID.randomUUID())).expectNextCount(0).verifyComplete();
    }

    @Test
    void createCrawler() {

        StepVerifier.create(crawlerManagerRepository.save(CrawlerTestDummyProvider.crawler.get())).expectNextMatches(
                c -> c.status().equals(CrawlerStatus.CREATED) && c.name().equals("Test Crawler")
                        && c.config().equals(CrawlerTestDummyProvider.crawlerConfig.get())).verifyComplete();
    }

}
