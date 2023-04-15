package at.hypercrawler.supplierservice.web.controller;

import at.hypercrawler.supplierservice.domain.exception.CrawlerAlreadyExistsException;
import at.hypercrawler.supplierservice.domain.exception.CrawlerNotFoundException;
import at.hypercrawler.supplierservice.web.exception.ApiError;
import at.hypercrawler.supplierservice.web.exception.ApiSubError;
import at.hypercrawler.supplierservice.web.exception.ApiValidationError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class CrawlerControllerAdvice{

    private ResponseEntity<HttpStatus> buildResponseEntity(ApiError apiError) {
        return ResponseEntity.status(apiError.status()).body(apiError.status());
    }

    @ExceptionHandler(CrawlerNotFoundException.class)
    ResponseEntity<HttpStatus> crawlerNotFoundHandler(CrawlerNotFoundException ex) {
        return buildResponseEntity(new ApiError(HttpStatus.NOT_FOUND, LocalDateTime.now(), ex.getLocalizedMessage()));
    }

    @ExceptionHandler(CrawlerAlreadyExistsException.class)
    ResponseEntity<HttpStatus> crawlerAlreadyExistsHandler(CrawlerAlreadyExistsException ex) {
        return buildResponseEntity(new ApiError(HttpStatus.UNPROCESSABLE_ENTITY, LocalDateTime.now(), ex.getLocalizedMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<HttpStatus> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        var errorString = "Request was malformed. Please see 'causes' for further information.";
        List<ApiSubError> apiSubErrors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> new ApiValidationError(((FieldError) error).getField(), ((FieldError) error).getRejectedValue(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, LocalDateTime.now(), errorString, apiSubErrors));
    }

}
