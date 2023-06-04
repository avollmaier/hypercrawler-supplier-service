package at.hypercrawler.managerservice.web;

import at.hypercrawler.managerservice.CrawlerTestDummyProvider;
import at.hypercrawler.managerservice.web.dto.CrawlerRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CrawlerRequestJsonTest {

  @Autowired
  private JacksonTester<CrawlerRequest> json;

  @Test
  void whenDeserialize_thenValidCrawlerObject()
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
                           "filterOptions":{
                              "siteExclusionPatterns":[
                                 "https://www.google.com/**"
                              ],
                              "queryParameterExclusionPatterns":[
                                 "utm_*"
                              ]
                           },
                           "requestOptions":{
                              "proxy": {
                                "host": "localhost",
                                "port": 8080
                              },
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
                           "actions":[
                              {
                                 "indexName":"test_index",
                                 "pathsToMatch":[
                                    "http://www.foufos.gr/*"
                                 ],
                                 "selectorsToMatch":[
                                    ".products",
                                    "!.featured"
                                 ],
                                 "contentTypesToMatch":[
                                    "HTML",
                                    "PDF"
                                 ]
                              }
                           ]
                        },
                        "createdAt":"2023-05-21T11:50:46.954593200Z",
                        "updatedAt":"2023-05-21T11:50:46.954593200Z"
                     }
                  """;
    assertThat(this.json.parse(content)).usingRecursiveComparison().isEqualTo(CrawlerTestDummyProvider.crawlerRequest.get());
  }

}