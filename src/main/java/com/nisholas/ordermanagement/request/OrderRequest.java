package com.nisholas.ordermanagement.request;

import com.nisholas.ordermanagement.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record OrderRequest(

        @NotNull(message = "Customer id is required")
        Long customerId,

        @NotNull(message = "Status is required")
        OrderStatus status
) {
}
