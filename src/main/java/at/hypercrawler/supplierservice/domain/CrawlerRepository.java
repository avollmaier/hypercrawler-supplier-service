package at.hypercrawler.supplierservice.domain;

import at.hypercrawler.supplierservice.domain.model.Crawler;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CrawlerRepository extends ReactiveMongoRepository<Crawler, UUID> {
}