package at.hypercrawler.managerservice.web.dto;

import at.hypercrawler.managerservice.domain.model.CrawlerConfig;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CrawlerRequest(

        @NotBlank(message = "Name could not be blank")
        String name,

        @NotNull(message = "Config could not be null")
        @Valid
        CrawlerConfig config

) {
}
