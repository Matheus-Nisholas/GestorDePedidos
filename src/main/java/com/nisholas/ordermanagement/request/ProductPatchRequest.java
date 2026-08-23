package com.nisholas.ordermanagement.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProductPatchRequest(

        String name,
        String description,

        @DecimalMin(value = "0.0", inclusive = true, message = "Price must be zero or greater")
        BigDecimal price,

        @PositiveOrZero(message = "Stock quantity must be zero or greater")
        Integer stockQuantity,

        Boolean active
) {
}
