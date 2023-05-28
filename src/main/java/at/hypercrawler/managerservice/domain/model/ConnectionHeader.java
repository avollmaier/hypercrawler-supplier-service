package at.hypercrawler.managerservice.domain.model;

import jakarta.validation.constraints.NotNull;

public record ConnectionHeader(

        @NotNull(message = "Request header name could not be null")
        String name,

        @NotNull(message = "Request header value could not be null")
        String value) {
}
