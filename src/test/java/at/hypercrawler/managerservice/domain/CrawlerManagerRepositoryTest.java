package at.hypercrawler.managerservice.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import at.hypercrawler.managerservice.domain.model.Crawler;
import at.hypercrawler.managerservice.web.dto.CrawlerAction;
import at.hypercrawler.managerservice.web.dto.CrawlerConfig;
import at.hypercrawler.managerservice.web.dto.CrawlerRequestOptions;
import at.hypercrawler.managerservice.web.dto.CrawlerRobotOptions;
import at.hypercrawler.managerservice.web.dto.CrawlerStatus;
import at.hypercrawler.managerservice.web.dto.Header;
import at.hypercrawler.managerservice.web.dto.SupportedFileType;
import reactor.test.StepVerifier;

@DataMongoTest
@Testcontainers
class CrawlerManagerRepositoryTest {
    @Container
    private static final MongoDBContainer mongoContainer =
      new MongoDBContainer(DockerImageName.parse("mongo:latest"));
    Supplier<List<String>> startUrls = () -> Arrays.asList("https://www.google.com", "https://www.bing.com");
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
        .siteExclusionPatterns(Collections.singletonList("https://www.google.com/**")).build();

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
        var createdCrawler = new Crawler("Test Crawler", CrawlerStatus.CREATED, crawlerConfig.get());
        StepVerifier.create(crawlerManagerRepository.save(createdCrawler)).expectNextMatches(
          c -> c.getStatus().equals(CrawlerStatus.CREATED) && c.getName().equals("Test Crawler")
            && c.getConfig().equals(crawlerConfig.get())).verifyComplete();
    }

}
