package at.hypercrawler.managerservice.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import at.hypercrawler.managerservice.web.dto.CrawlerAction;
import at.hypercrawler.managerservice.web.dto.CrawlerConfig;
import at.hypercrawler.managerservice.web.dto.CrawlerRequest;
import at.hypercrawler.managerservice.web.dto.CrawlerRequestOptions;
import at.hypercrawler.managerservice.web.dto.CrawlerRobotOptions;
import at.hypercrawler.managerservice.web.dto.Header;
import at.hypercrawler.managerservice.web.dto.SupportedFileType;

@JsonTest
class CrawlerRequestJsonTest {

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
            () -> CrawlerConfig.builder().action(crawlerAction.get())
                    .indexPrefix("crawler_").requestOptions(crawlerRequestOptions.get()).startUrls(startUrls.get())
                    .schedule("0 0 0 1 1 ? 2099").robotOptions(robotOptions.get()).build();
    Supplier<CrawlerRequest> crawlerRequest = () -> new CrawlerRequest("Test Crawler", crawlerConfig.get());
    @Autowired
    private JacksonTester<CrawlerRequest> json;

    @Test
    void testDeserialize()
            throws Exception {
        var content = """
                {
                      "id":"ae683f02-7a5c-4a79-9c5c-ab93770c46f5",
                      "name":"Test Crawler",
                      "status":"CREATED",
                      "config":{
                         "indexPrefix":"crawler_",
                         "schedule":"0 0 0 1 1 ? 2099",
                         "startUrls":[
                            "https://www.google.com",
                            "https://www.bing.com"
                         ],
                         "requestOptions":{
                            "proxy":"http://localhost:8080",
                            "requestTimeout":1000,
                            "retries":3,
                            "headers":[
                               {
                                  "name":"User-Agent",
                                  "value":"Mozilla/5.0 (compatible"
                               }
                            ]
                         },
                         "robotOptions":{
                            "ignoreRobotRules":true,
                            "ignoreRobotNoIndex":true,
                            "ignoreRobotNoFollowTo":true
                         },
                         "action":{
                            "indexName":"test_index",
                            "pathsToMatch":[
                               "http://www.foufos.gr/**"
                            ],
                            "selectorsToMatch":[
                               ".products",
                               "!.featured"
                            ],
                            "fileTypesToMatch":[
                               "HTML",
                               "PDF"
                            ]
                         }
                      },
                      "createdAt":"2023-05-21T11:50:46.954593200Z",
                      "updatedAt":"2023-05-21T11:50:46.954593200Z"
                   }
                """;
        assertThat(this.json.parse(content)).usingRecursiveComparison().isEqualTo(crawlerRequest.get());
    }

}