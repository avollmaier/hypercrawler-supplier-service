package at.hypercrawler.managerservice.domain.model;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

public record ConnectionProxy(

        @NotBlank(message = "Host could not be blank")
        String host,

        @Range(min = 0, max = 65535, message = "Port must be between 0 and 65535")
        Integer port
) {
}
