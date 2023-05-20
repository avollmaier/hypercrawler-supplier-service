package at.hypercrawler.managerservice.domain.model;

public enum SupportedFileType {
    HTML("html"), PDF("pdf"), TXT("txt");

    private final String format;

    SupportedFileType(String format) {
        this.format = format;
    }
}
