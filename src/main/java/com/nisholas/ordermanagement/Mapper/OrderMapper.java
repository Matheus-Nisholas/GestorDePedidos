package com.nisholas.ordermanagement.Mapper;

import com.nisholas.ordermanagement.entity.OrderItem;
import com.nisholas.ordermanagement.request.OrderRequest;
import com.nisholas.ordermanagement.response.OrderResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OrderMapper {

    public static OrderItem toOrder(OrderRequest orderRequest) {
        OrderItem order = new OrderItem();
        order.setStatus(orderRequest.status());
        return order;
    }

    public static OrderResponse toOrderResponse(OrderItem order) {
        return OrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .build();
    }
}
