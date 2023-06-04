package at.hypercrawler.managerservice;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import at.hypercrawler.managerservice.domain.model.ConnectionHeader;
import at.hypercrawler.managerservice.domain.model.ConnectionProxy;
import at.hypercrawler.managerservice.domain.model.Crawler;
import at.hypercrawler.managerservice.domain.model.CrawlerAction;
import at.hypercrawler.managerservice.domain.model.CrawlerConfig;
import at.hypercrawler.managerservice.domain.model.CrawlerFilterOptions;
import at.hypercrawler.managerservice.domain.model.CrawlerRequestOptions;
import at.hypercrawler.managerservice.domain.model.CrawlerRobotOptions;
import at.hypercrawler.managerservice.domain.model.CrawlerStatus;
import at.hypercrawler.managerservice.domain.model.SupportedContentMediaType;
import at.hypercrawler.managerservice.web.dto.CrawlerRequest;

public class CrawlerTestDummyProvider {

    public static Supplier<List<String>> startUrls =
            () -> Arrays.asList("https://www.google.com", "https://www.bing.com");
    public static Supplier<List<String>> updatedStartUrls =
            () -> Arrays.asList("https://www.google.com", "https://www.bing.com", "https://www.yahoo.com");

    public static Supplier<List<SupportedContentMediaType>> contentTypesToMatch =
            () -> Arrays.asList(SupportedContentMediaType.HTML, SupportedContentMediaType.PDF);
    public static Supplier<List<SupportedContentMediaType>> updatedContentTypesToMatch =
            () -> Arrays.asList(SupportedContentMediaType.HTML, SupportedContentMediaType.PDF, SupportedContentMediaType.TXT);

    public static Supplier<List<String>> pathsToMatch =
            () -> List.of("http://www.foufos.gr/*");
    public static Supplier<List<String>> updatedPathsToMatch =
            () -> List.of("http://www.foufos.gr/*", "http://www.foufos.gr/*");

    public static Supplier<List<String>> selectorsToMatch =
            () -> Arrays.asList(".products", "!.featured");
    public static Supplier<List<String>> updatedSelectorsToMatch =
            () -> Arrays.asList(".noproducts", "!.featured", ".feature");

    public static Supplier<CrawlerAction> crawlerAction =
            () -> CrawlerAction.builder()
                    .contentTypesToMatch(contentTypesToMatch.get())
                    .pathsToMatch(pathsToMatch.get())
                    .selectorsToMatch(selectorsToMatch.get())
                    .indexName("test_index")
                    .build();
    public static Supplier<CrawlerAction> updatedCrawlerAction =
            () -> CrawlerAction.builder()
                    .contentTypesToMatch(updatedContentTypesToMatch.get())
                    .pathsToMatch(updatedPathsToMatch.get())
                    .selectorsToMatch(updatedSelectorsToMatch.get())
                    .indexName("test2_index")
                    .build();


    public static Supplier<ConnectionProxy> connectionProxy =
            () -> new ConnectionProxy("localhost", 8080);

    public static Supplier<CrawlerRequestOptions> crawlerRequestOptions =
            () -> CrawlerRequestOptions.builder()
                    .requestTimeout(1000)
                    .proxy(connectionProxy.get())
                    .retries(3)
                    .headers(Collections.singletonList(new ConnectionHeader("User-Agent", "Mozilla/5.0 (compatible")))
                    .build();
    public static Supplier<CrawlerRequestOptions> updatedCrawlerRequestOptions =
            () -> CrawlerRequestOptions.builder().requestTimeout(11000)
                    .proxy(connectionProxy.get())
                    .retries(32)
                    .headers(Collections.singletonList(new ConnectionHeader("User-Agent", "Chrome/5.0 (compatible")))
                    .build();


    public static Supplier<CrawlerRobotOptions> robotOptions =
            () -> CrawlerRobotOptions.builder()
                    .ignoreRobotNoFollowTo(true)
                    .ignoreRobotRules(true)
                    .ignoreRobotNoIndex(true).build();
    public static Supplier<CrawlerRobotOptions> updatedRobotOptions =
            () -> CrawlerRobotOptions.builder()
                    .ignoreRobotNoFollowTo(false)
                    .ignoreRobotRules(true)
                    .ignoreRobotNoIndex(false).build();

    public static Supplier<CrawlerFilterOptions> crawlerFilterOptions =
            () -> CrawlerFilterOptions.builder()
              .queryParameterExclusionPatterns(Collections.singletonList("utm_*"))
              .siteExclusionPatterns(Collections.singletonList("https://www.google.com/*"))
                    .build();
    public static Supplier<CrawlerFilterOptions> updatedCrawlerFilterOptions =
            () -> CrawlerFilterOptions.builder()
              .queryParameterExclusionPatterns(Collections.singletonList("utc_*"))
              .siteExclusionPatterns(Collections.singletonList("https://www.yahoo.com/*"))
                    .build();

    public static Supplier<CrawlerConfig> crawlerConfig =
            () -> CrawlerConfig.builder()
                    .actions(Collections.singletonList(crawlerAction.get()))
                    .indexPrefix("crawler_")
                    .requestOptions(crawlerRequestOptions.get())
                    .startUrls(startUrls.get())
                    .schedule("0 0 0 1 1 ? 2099")
                    .robotOptions(robotOptions.get())
                    .filterOptions(crawlerFilterOptions.get()).build();
    public static Supplier<CrawlerConfig> updatedCrawlerConfig =
            () -> CrawlerConfig.builder()
                    .actions(Collections.singletonList(updatedCrawlerAction.get()))
                    .indexPrefix("crawlerr_")
                    .requestOptions(updatedCrawlerRequestOptions.get())
                    .startUrls(updatedStartUrls.get()).schedule("0 0 2 1 1 ? 2099")
                    .robotOptions(updatedRobotOptions.get())
                    .filterOptions(updatedCrawlerFilterOptions.get()).build();

    public static Supplier<CrawlerRequest> crawlerRequest =
            () -> new CrawlerRequest("Test Crawler", crawlerConfig.get());

    public static Supplier<CrawlerRequest> updatedCrawlerRequest =
            () -> new CrawlerRequest("Updated Test Crawler", updatedCrawlerConfig.get());

    public static Supplier<Crawler> crawler =
            () -> new Crawler("Test Crawler", CrawlerStatus.CREATED, crawlerConfig.get());

    public static Supplier<Crawler> updatedCrawler =
            () -> new Crawler("Updated Test Crawler", CrawlerStatus.CREATED, updatedCrawlerConfig.get());

}
