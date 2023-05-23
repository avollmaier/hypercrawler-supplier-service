package at.hypercrawler.managerservice.web.dto;

public enum SupportedFileType {
    HTML("html"), PDF("pdf"), TXT("txt");

    private final String format;

    SupportedFileType(String format) {
        this.format = format;
    }
}
