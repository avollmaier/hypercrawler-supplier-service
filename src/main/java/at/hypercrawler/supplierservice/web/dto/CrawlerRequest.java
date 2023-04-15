package at.hypercrawler.supplierservice.web.dto;

import jakarta.validation.constraints.NotBlank;


public record CrawlerRequest (
    @NotBlank(message = "Name must not be blank")
    String name
){}
