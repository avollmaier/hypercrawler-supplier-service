package at.hypercrawler.managerservice.web.dto;

import at.hypercrawler.managerservice.domain.model.CrawlerConfig;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CrawlerRequest(

        @NotBlank(message = "Name must not be blank")
        String name,

        @NotNull
        @Valid
        CrawlerConfig config

) {
}
