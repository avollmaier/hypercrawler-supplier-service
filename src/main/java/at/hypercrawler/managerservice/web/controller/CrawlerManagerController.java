package at.hypercrawler.managerservice.web.controller;

import at.hypercrawler.managerservice.domain.model.SupportedFileType;
import at.hypercrawler.managerservice.domain.service.CrawlerManagerService;
import at.hypercrawler.managerservice.web.dto.CrawlerRequest;
import at.hypercrawler.managerservice.web.dto.CrawlerResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("crawlers")
public class CrawlerManagerController {
    private final CrawlerManagerService crawlerManagerService;

    private final CrawlerRequestMapper crawlerRequestMapper;

    private final CrawlerResponseMapper crawlerResponseMapper;

    public CrawlerManagerController(CrawlerManagerService crawlerManagerService, CrawlerRequestMapper crawlerRequestMapper,
                                    CrawlerResponseMapper crawlerResponseMapper) {
        this.crawlerManagerService = crawlerManagerService;
        this.crawlerRequestMapper = crawlerRequestMapper;
        this.crawlerResponseMapper = crawlerResponseMapper;
    }

    @GetMapping
    Flux<CrawlerResponse> get() {
        log.info("Fetching the list of all crawlers");
        return crawlerManagerService.findAll().map(crawlerResponseMapper);
    }

    @GetMapping("{uuid}")
    Mono<CrawlerResponse> getByUuid(@PathVariable UUID uuid) {
        log.info("Fetching the crawler with uuid {}", uuid);
        return crawlerManagerService.findById(uuid).map(crawlerResponseMapper);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Mono<CrawlerResponse> create(@Valid @RequestBody CrawlerRequest crawlerRequest) {
        log.info("Creating a new crawler with name {}", crawlerRequest.name());
        var crawler = crawlerRequestMapper.apply(crawlerRequest);
        return crawlerManagerService.createCrawler(crawler).map(crawlerResponseMapper);
    }

    @DeleteMapping("{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    Mono<Void> delete(@PathVariable UUID uuid) {
        log.info("Deleting the crawler with uuid {}", uuid);
        return crawlerManagerService.deleteCrawler(uuid);
    }

    @PutMapping(value = "{uuid}")
    Mono<CrawlerResponse> update(@PathVariable UUID uuid, @Valid @RequestBody CrawlerRequest crawlerRequest) {
        log.info("Updating the crawler with uuid {}", uuid);
        return crawlerManagerService.updateCrawler(uuid, crawlerRequest.name(), crawlerRequest.config()).map(crawlerResponseMapper);
    }

    @PutMapping(value = "{uuid}/start")
    Mono<CrawlerResponse> start(@PathVariable UUID uuid) {
        log.info("Starting the crawler with uuid {}", uuid);
        return crawlerManagerService.startCrawler(uuid).map(crawlerResponseMapper);
    }

    @PutMapping(value = "{uuid}/stop")
    Mono<CrawlerResponse> stop(@PathVariable UUID uuid) {
        log.info("Stopping the crawler with uuid {}", uuid);
        return crawlerManagerService.stopCrawler(uuid).map(crawlerResponseMapper);
    }

}
