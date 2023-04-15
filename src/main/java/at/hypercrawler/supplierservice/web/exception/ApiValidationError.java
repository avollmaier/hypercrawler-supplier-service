package at.hypercrawler.supplierservice.web.exception;

public record ApiValidationError(String field, Object rejectedValue, String message) implements ApiSubError {
}
