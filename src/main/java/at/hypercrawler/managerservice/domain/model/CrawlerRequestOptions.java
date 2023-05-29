package at.hypercrawler.managerservice.domain.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record CrawlerRequestOptions(

        @Valid
        ConnectionProxy proxy,

        @Min(value = 1, message = "Request timeout must be greater than 0")
        @NotNull(message = "Request timeout could not be null")
        Integer requestTimeout,

        @Min(value = 1, message = "Retries must be greater than 0")
        @NotNull(message = "Request retries could not be null")
        Integer retries,

        List<@Valid @NotNull(message = "Request header could not be null") ConnectionHeader> headers
) {
}
