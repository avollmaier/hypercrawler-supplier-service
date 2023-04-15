package at.hypercrawler.supplierservice.web.controller;

import at.hypercrawler.supplierservice.domain.model.CrawlerStatus;
import at.hypercrawler.supplierservice.domain.service.CrawlerService;
import at.hypercrawler.supplierservice.web.dto.CrawlerRequest;
import at.hypercrawler.supplierservice.web.dto.CrawlerResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("crawlers")
public class CrawlerController {
    private final CrawlerService crawlerService;

    private final CrawlerRequestMapper crawlerRequestMapper;

    private final CrawlerResponseMapper crawlerResponseMapper;

    public CrawlerController(CrawlerService crawlerService, CrawlerRequestMapper crawlerRequestMapper, CrawlerResponseMapper crawlerResponseMapper) {
        this.crawlerService = crawlerService;
        this.crawlerRequestMapper = crawlerRequestMapper;
        this.crawlerResponseMapper = crawlerResponseMapper;
    }

    @GetMapping
    Flux<CrawlerResponse> get() {
        log.info("Fetching the list of all crawlers");
        return crawlerService.findAll().map(crawlerResponseMapper);
    }

    @GetMapping("{uuid}")
    Mono<CrawlerResponse> getByUuid(@PathVariable UUID uuid) {
        log.info("Fetching the crawler with uuid {}", uuid);
        return crawlerService.findById(uuid).map(crawlerResponseMapper);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Mono<CrawlerResponse> create(@Valid @RequestBody CrawlerRequest crawlerRequest) {
        log.info("Creating a new crawler with name {}", crawlerRequest.name());
        var crawler = crawlerRequestMapper.apply(crawlerRequest);
        return crawlerService.createCrawler(crawler).map(crawlerResponseMapper);
    }

    @DeleteMapping("{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    Mono<Void> delete(@PathVariable UUID uuid) {
        log.info("Deleting the crawler with uuid {}", uuid);
        return crawlerService.deleteCrawler(uuid);
    }

    @PostMapping(value = "{uuid}/run")
    Mono<CrawlerResponse> run(@PathVariable UUID uuid) {
        log.info("Updating the crawler status with uuid {} to {}", uuid, CrawlerStatus.RUNNING);
        return crawlerService.updateCrawler(uuid,CrawlerStatus.RUNNING).map(crawlerResponseMapper);
    }

    @PostMapping(value = "{uuid}/pause")
    Mono<CrawlerResponse> pause(@PathVariable UUID uuid) {
        log.info("Updating the crawler status with uuid {} to {}", uuid, CrawlerStatus.STOPPED);
        return crawlerService.updateCrawler(uuid,CrawlerStatus.STOPPED).map(crawlerResponseMapper);
    }
}
