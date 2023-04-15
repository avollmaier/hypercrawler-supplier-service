package at.hypercrawler.supplierservice.web.exception;

import at.hypercrawler.supplierservice.domain.exception.CrawlerAlreadyExistsException;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

public record ApiError(
        HttpStatus status,

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime timestamp,

        String message,

        @JsonInclude(JsonInclude.Include.NON_NULL) List<ApiSubError> causes

) {
    public ApiError(HttpStatus status, LocalDateTime timestamp, String message) {
        this(status, timestamp, message, null);
    }
}
