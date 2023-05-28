package at.hypercrawler.managerservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;

@Getter
public enum SupportedFileType {
    HTML("html"), PDF("pdf"), TXT("txt");

    private final String format;

    SupportedFileType(String format) {
        this.format = format;
    }
}
