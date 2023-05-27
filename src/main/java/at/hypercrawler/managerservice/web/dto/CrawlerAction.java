package at.hypercrawler.managerservice.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Value
@Builder
public class CrawlerAction {

  @NotBlank(message = "Index name could not be empty") String indexName;

  @NotNull(message = "Paths to match during crawl could not be empty") List<@NotNull(message = "Paths to match during crawl could not be empty") String>
    pathsToMatch;

  List<@NotBlank(message = "Selectors to match during crawl could not be empty") String> selectorsToMatch;

  List<@NotNull(message = "File type to match during crawl could not be empty") SupportedFileType>
    fileTypesToMatch;

}
