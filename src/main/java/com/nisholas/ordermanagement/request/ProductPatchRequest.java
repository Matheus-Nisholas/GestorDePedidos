package com.nisholas.ordermanagement.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductPatchRequest(

        @Size(max = 100, message = "Name must have at most 100 characters")
        String name,

        @Size(max = 255, message = "Description must have at most 255 characters")
        String description,

        @DecimalMin(value = "0.00", inclusive = true, message = "Price must be greater than or equal to zero")
        BigDecimal price,

        @PositiveOrZero(message = "Stock quantity must be greater than or equal to zero")
        Integer stockQuantity,

        Boolean active
) {
}
