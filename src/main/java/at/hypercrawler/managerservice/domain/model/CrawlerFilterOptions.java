package at.hypercrawler.managerservice.domain.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.List;

@Builder
public record CrawlerFilterOptions(

        List<@NotBlank(message = "Exclusion-Pattern could not be empty") String> siteExclusionPatterns,

        List<@NotBlank(message = "Ignored-Query-Parameter could not be empty") String> queryParameterExclusionPatterns

) {
}
