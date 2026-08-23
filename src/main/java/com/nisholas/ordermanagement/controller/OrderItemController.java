package com.nisholas.ordermanagement.controller;

import com.nisholas.ordermanagement.Mapper.OrderItemMapper;
import com.nisholas.ordermanagement.entity.OrderItem;
import com.nisholas.ordermanagement.request.OrderItemRequest;
import com.nisholas.ordermanagement.response.OrderItemResponse;
import com.nisholas.ordermanagement.service.OrderItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order-items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    @GetMapping
    public ResponseEntity<List<OrderItemResponse>> getAllOrderItems() {
        List<OrderItemResponse> orderItems = orderItemService.findAll()
                .stream()
                .map(OrderItemMapper::toOrderItemResponse)
                .toList();

        return ResponseEntity.ok(orderItems);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItemResponse> getByOrderItemId(@PathVariable Long id) {
        return ResponseEntity.ok(orderItemService.getByOrderItemId(id));
    }

    @PostMapping
    public ResponseEntity<OrderItemResponse> saveOrderItem(
            @Valid @RequestBody OrderItemRequest request) {

        OrderItem orderItem = orderItemService.saveOrderItem(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(OrderItemMapper.toOrderItemResponse(orderItem));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OrderItemResponse> deleteByOrderItemId(@PathVariable Long id) {
        return ResponseEntity.ok(orderItemService.deleteByOrderItemId(id));
    }
}
