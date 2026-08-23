package com.nisholas.ordermanagement.request;

import com.nisholas.ordermanagement.entity.OrderStatus;

public record OrderPatchRequest(
        Long customerId,
        OrderStatus status
) {
}
