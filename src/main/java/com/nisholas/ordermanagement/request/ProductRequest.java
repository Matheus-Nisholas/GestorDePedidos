package com.nisholas.ordermanagement.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductRequest(

        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must have at most 100 characters")
        String name,

        @Size(max = 255, message = "Description must have at most 255 characters")
        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.00", inclusive = true, message = "Price must be greater than or equal to zero")
        BigDecimal price,

        @NotNull(message = "Stock quantity is required")
        @PositiveOrZero(message = "Stock quantity must be greater than or equal to zero")
        Integer stockQuantity,

        @NotNull(message = "Active status is required")
        Boolean active
) {
}
