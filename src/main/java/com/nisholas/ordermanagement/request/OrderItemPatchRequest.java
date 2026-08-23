package com.nisholas.ordermanagement.request;

import jakarta.validation.constraints.Positive;

public record OrderItemPatchRequest(
        Long orderId,
        Long productId,

        @Positive(message = "Quantity must be greater than zero")
        Integer quantity
) {
}
