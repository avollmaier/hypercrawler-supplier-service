package at.hypercrawler.supplierservice.domain;

import at.hypercrawler.supplierservice.domain.model.Crawler;
import at.hypercrawler.supplierservice.domain.model.CrawlerStatus;
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
public class CrawlerRepositoryTest {
    @Container
    private static final MongoDBContainer mongoContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"));

    @Autowired
    private CrawlerRepository crawlerRepository;


    @DynamicPropertySource
    static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoContainer::getReplicaSetUrl);
    }

    @Test
    public void findOrderByIdWhenNotExisting() {
        StepVerifier.create(crawlerRepository.findById(UUID.randomUUID())).expectNextCount(0).verifyComplete();
    }

    @Test
    void createCrawler() {
        var createdCrawler = new Crawler("Test Crawler", CrawlerStatus.CREATED);
        StepVerifier.create(crawlerRepository.save(createdCrawler)).expectNextMatches(c -> c.status().equals(CrawlerStatus.CREATED) && c.name().equals("Test Crawler")).verifyComplete();
    }


}
