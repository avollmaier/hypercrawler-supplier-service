package at.hypercrawler.managerservice.web.controller;

import java.util.UUID;

import at.hypercrawler.managerservice.web.dto.StatusResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import at.hypercrawler.managerservice.domain.service.CrawlerManagerService;
import at.hypercrawler.managerservice.web.dto.CrawlerRequest;
import at.hypercrawler.managerservice.web.dto.CrawlerResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    //get status by uuid
    @GetMapping("{uuid}/status")
    Mono<StatusResponse> getStatusByUuid(@PathVariable UUID uuid) {
        log.info("Fetching the status of the crawler with uuid {}", uuid);
        return crawlerManagerService.findById(uuid).map(crawlerResponseMapper).map(crawlerResponse -> new StatusResponse(crawlerResponse.getStatus()));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Mono<CrawlerResponse> create(@Valid @RequestBody CrawlerRequest crawlerRequest) {
        log.info("Creating a new crawler with name {}", crawlerRequest.getName());
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
        return crawlerManagerService.updateCrawler(uuid, crawlerRequest.getName(), crawlerRequest.getConfig())
          .map(crawlerResponseMapper);
    }

    @PutMapping(value = "{uuid}/run")
    Mono<CrawlerResponse> start(@PathVariable UUID uuid) {
        log.info("Starting the crawler with uuid {}", uuid);
        return crawlerManagerService.startCrawler(uuid).map(crawlerResponseMapper);
    }

    @PutMapping(value = "{uuid}/pause")
    Mono<CrawlerResponse> stop(@PathVariable UUID uuid) {
        log.info("Stopping the crawler with uuid {}", uuid);
        return crawlerManagerService.stopCrawler(uuid).map(crawlerResponseMapper);
    }

}
