package com.nisholas.ordermanagement.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemRequest(

        @NotNull(message = "Order id is required")
        Long orderId,

        @NotNull(message = "Product id is required")
        Long productId,

        @Positive(message = "Quantity must be greater than zero")
        int quantity
) {
}
