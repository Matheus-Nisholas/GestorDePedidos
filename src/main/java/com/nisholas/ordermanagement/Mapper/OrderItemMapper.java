package com.nisholas.ordermanagement.Mapper;

import com.nisholas.ordermanagement.entity.OrderItem;
import com.nisholas.ordermanagement.response.OrderItemResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OrderItemMapper {

    public static OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .id(orderItem.getId())
                .orderId(orderItem.getOrder().getId())
                .productId(orderItem.getProduct().getId())
                .quantity(orderItem.getQuantity())
                .unitPrice(orderItem.getUnitPrice())
                .subtotal(orderItem.getSubtotal())
                .build();
    }
}
