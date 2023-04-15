package at.hypercrawler.supplierservice.domain.service;

import at.hypercrawler.supplierservice.domain.exception.CrawlerAlreadyExistsException;
import at.hypercrawler.supplierservice.domain.exception.CrawlerNotFoundException;
import at.hypercrawler.supplierservice.domain.model.Crawler;
import at.hypercrawler.supplierservice.domain.CrawlerRepository;
import at.hypercrawler.supplierservice.domain.model.CrawlerStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class CrawlerService {
    private final CrawlerRepository crawlerRepository;

    public CrawlerService(CrawlerRepository crawlerRepository) {
        this.crawlerRepository = crawlerRepository;
    }

    public Flux<Crawler> findAll() {
        return crawlerRepository.findAll();
    }

    public Mono<Crawler> findById(UUID uuid) {
        return crawlerRepository.findById(uuid).switchIfEmpty(Mono.error(new CrawlerNotFoundException(uuid)));
    }

    public Mono<Crawler> createCrawler(Crawler crawler) {
        return crawlerRepository.existsById(crawler.id()).flatMap(exists -> {
            if (exists) {
                return Mono.error(new CrawlerAlreadyExistsException(crawler.id()));
            }
            return crawlerRepository.save(crawler);
        });
    }

    public Mono<Crawler> updateCrawler(UUID uuid, CrawlerStatus status) {
        // @formatter:off
        return crawlerRepository.findById(uuid).flatMap(c -> {
            var updatedEntity = new Crawler(
                    c.id(),
                    c.name(),
                    status,
                    c.createdAt(),
                    c.updatedAt());
            return crawlerRepository.save(updatedEntity);
        }).switchIfEmpty(Mono.error(new CrawlerNotFoundException(uuid)));
        // @formatter:on
    }

    public Mono<Void> deleteCrawler(UUID uuid) {
        return crawlerRepository.deleteById(uuid);
    }
}
