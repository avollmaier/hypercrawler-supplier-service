package at.hypercrawler.managerservice.domain.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record CrawlerAction(

        @NotBlank(message = "Index name could not be blank")
        String indexName,

        @NotNull(message = "Paths to match during crawl could not be null")
        List<@NotNull(message = "Path to match could not be null") String> pathsToMatch,

        List<@NotBlank(message = "Selectors to match during crawl could not be blank") String> selectorsToMatch,

        List<@NotNull(message = "File type to match during crawl could not be null") SupportedFileType> fileTypesToMatch

) {


}
