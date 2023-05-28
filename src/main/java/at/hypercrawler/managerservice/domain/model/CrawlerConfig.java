package at.hypercrawler.managerservice.domain.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

import java.util.List;

@Builder
public record CrawlerConfig(

        @NotBlank(message = "Index prefix could not be empty") String indexPrefix,

        @Pattern(regexp = "^((((\\d+,)+\\d+|(\\d+(\\/|-|#)\\d+)|\\d+L?|\\*(\\/\\d+)?|L(-\\d+)?|\\?|[A-Z]{3}(-[A-Z]{3})?) ?){5,7})$", message = "Schedule is not valid")
        String schedule,

        @NotNull(message = "Start-Urls could not be empty")
        List<@NotBlank(message = "Start-Url could not be empty") String> startUrls,

        @Valid CrawlerFilterOptions filterOptions,

        @Valid CrawlerRequestOptions requestOptions,

        @Valid CrawlerRobotOptions robotOptions,

        @NotNull(message = "Actions could not be empty")
        List<@NotNull(message = "Actions could not be empty") CrawlerAction> actions

) {
}
