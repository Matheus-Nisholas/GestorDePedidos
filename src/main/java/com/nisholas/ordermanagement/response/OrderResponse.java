package com.nisholas.ordermanagement.response;

import com.nisholas.ordermanagement.entity.OrderStatus;
import lombok.Builder;

@Builder
public record OrderResponse(
        Long id,
        OrderStatus status
) {
}
