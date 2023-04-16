package at.hypercrawler.supplierservice.domain.model;

public enum SupportedFileType {
    HTML("html"), PDF("pdf"), DOC("doc"), DOCX("docx"), XLS("xls"), XLSX("xlsx"), PPT("ppt"), PPTX("pptx"), TXT("txt");

    private final String format;

    SupportedFileType(String format) {
        this.format = format;
    }
}
