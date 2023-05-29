package at.hypercrawler.managerservice.domain.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.List;

@Builder
public record CrawlerFilterOptions(

        List<@NotBlank(message = "Exclusion-Pattern could not be blank") String> siteExclusionPatterns,

        List<@NotBlank(message = "Ignored-Query-Parameter could not be blank") String> queryParameterExclusionPatterns

) {
}
