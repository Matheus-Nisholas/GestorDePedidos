package com.nisholas.ordermanagement.controller;

import com.nisholas.ordermanagement.Mapper.OrderMapper;
import com.nisholas.ordermanagement.entity.Order;
import com.nisholas.ordermanagement.request.OrderPatchRequest;
import com.nisholas.ordermanagement.request.OrderRequest;
import com.nisholas.ordermanagement.response.OrderResponse;
import com.nisholas.ordermanagement.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = orderService.findAll()
                .stream()
                .map(OrderMapper::toOrderResponse)
                .toList();

        return ResponseEntity.ok(orders);
    }

    @PostMapping
    public ResponseEntity<OrderResponse> saveOrder(@Valid @RequestBody OrderRequest request) {
        Order savedOrder = orderService.saveOrder(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(OrderMapper.toOrderResponse(savedOrder));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getByOrderId(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getByOrderId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OrderResponse> deleteByOrderId(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.deleteByOrderId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> putByOrderId(
            @PathVariable Long id,
            @Valid @RequestBody OrderRequest request) {

        Order order = orderService.putByOrderId(id, request);
        return ResponseEntity.ok(OrderMapper.toOrderResponse(order));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrderResponse> patchOrder(
            @PathVariable Long id,
            @RequestBody OrderPatchRequest request) {

        Order order = orderService.patchOrder(id, request);
        return ResponseEntity.ok(OrderMapper.toOrderResponse(order));
    }
}
