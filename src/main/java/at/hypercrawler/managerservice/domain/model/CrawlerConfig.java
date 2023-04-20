package at.hypercrawler.managerservice.domain.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CrawlerConfig(@NotNull List<@NotBlank(message = "Start-Url could not be empty") String> startUrls,
                            List<@NotNull(message = "File type to match during crawl could not be empty") SupportedFileType> fileTypesToMatch,
                            List<@NotNull(message = "Paths to match during crawl could not be empty") String> pathsToMatch,
                            List<@NotBlank(message = "Selectors to match during crawl could not be empty") String> selectorsToMatch) {
}
