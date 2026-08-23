package com.nisholas.ordermanagement.Mapper;

import com.nisholas.ordermanagement.entity.Order;
import com.nisholas.ordermanagement.request.OrderRequest;
import com.nisholas.ordermanagement.response.OrderResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OrderMapper {

    public static Order toOrder(OrderRequest orderRequest) {
        return Order.builder()
                .status(orderRequest.status())
                .build();
    }

    public static OrderResponse toOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .build();
    }
}
