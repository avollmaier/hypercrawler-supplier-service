package at.hypercrawler.supplierservice.domain;

import at.hypercrawler.supplierservice.domain.model.Crawler;
import at.hypercrawler.supplierservice.domain.model.CrawlerConfig;
import at.hypercrawler.supplierservice.domain.model.CrawlerStatus;
import at.hypercrawler.supplierservice.domain.model.SupportedFileType;
import at.hypercrawler.supplierservice.web.dto.CrawlerRequest;
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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@DataMongoTest
@Testcontainers
class CrawlerRepositoryTest {
    @Container
    private static final MongoDBContainer mongoContainer =
            new MongoDBContainer(DockerImageName.parse("mongo:latest"));

    @Autowired
    private CrawlerRepository crawlerRepository;

    @DynamicPropertySource
    static void mongoDbProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoContainer::getReplicaSetUrl);
    }

    Supplier<List<String>> startUrls = () -> Arrays.asList("https://www.google.com", "https://www.bing.com");
    Supplier<List<SupportedFileType>> fileTypesToMatch = () -> Arrays.asList(SupportedFileType.HTML, SupportedFileType.PDF);
    Supplier<List<String>> pathsToMatch = () -> Arrays.asList("/path/to/file.html", "/test2/**");
    Supplier<List<String>> selectorsToMatch = () -> Arrays.asList("selector1", "selector2");
    Supplier<CrawlerConfig> crawlerConfig = () -> new CrawlerConfig(startUrls.get(), fileTypesToMatch.get(), pathsToMatch.get(), selectorsToMatch.get());
    Supplier<CrawlerRequest> crawlerRequest = () -> new CrawlerRequest("Test Crawler", crawlerConfig.get());


    @Test
    void findCrawlerByIdWhenNotExisting() {
        StepVerifier.create(crawlerRepository.findById(UUID.randomUUID())).expectNextCount(0).verifyComplete();
    }

    @Test
    void createCrawler() {
        var createdCrawler = new Crawler("Test Crawler", CrawlerStatus.CREATED, crawlerConfig.get());
        StepVerifier.create(crawlerRepository.save(createdCrawler))
                .expectNextMatches(c -> c.status().equals(CrawlerStatus.CREATED) && c.name().equals("Test Crawler") && c.config().equals(crawlerConfig.get()))
                .verifyComplete();
    }

}
