package com.nisholas.ordermanagement.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItemResponse(
        Long id,
        Long orderId,
        Long productId,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
}
