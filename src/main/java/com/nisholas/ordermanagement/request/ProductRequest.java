package com.nisholas.ordermanagement.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProductRequest(

        @NotBlank(message = "Name is required")
        String name,

        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Price must be zero or greater")
        BigDecimal price,

        @PositiveOrZero(message = "Stock quantity must be zero or greater")
        int stockQuantity,

        boolean active
) {
}
