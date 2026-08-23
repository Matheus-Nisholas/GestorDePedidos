package com.nisholas.ordermanagement.service;

import com.nisholas.ordermanagement.Mapper.OrderItemMapper;
import com.nisholas.ordermanagement.entity.Order;
import com.nisholas.ordermanagement.entity.OrderItem;
import com.nisholas.ordermanagement.entity.Product;
import com.nisholas.ordermanagement.exception.InsufficientStockException;
import com.nisholas.ordermanagement.exception.ResourceNotFoundException;
import com.nisholas.ordermanagement.repository.OrderItemRepository;
import com.nisholas.ordermanagement.repository.OrderRepository;
import com.nisholas.ordermanagement.repository.ProductRepository;
import com.nisholas.ordermanagement.request.OrderItemRequest;
import com.nisholas.ordermanagement.response.OrderItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public List<OrderItem> findAll() {
        return orderItemRepository.findAll();
    }

    public OrderItemResponse getByOrderItemId(Long id) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order item not found with id: " + id
                ));

        return OrderItemMapper.toOrderItemResponse(orderItem);
    }

    @Transactional
    public OrderItem saveOrderItem(OrderItemRequest request) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + request.orderId()
                ));

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + request.productId()
                ));

        if (product.getStockQuantity() < request.quantity()) {
            throw new InsufficientStockException(
                    "Insufficient stock for product id: " + product.getId()
            );
        }

        BigDecimal unitPrice = product.getPrice();
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(request.quantity()));

        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .product(product)
                .quantity(request.quantity())
                .unitPrice(unitPrice)
                .subtotal(subtotal)
                .build();

        product.setStockQuantity(product.getStockQuantity() - request.quantity());
        order.setTotalAmount(order.getTotalAmount().add(subtotal));

        productRepository.save(product);
        orderRepository.save(order);

        return orderItemRepository.save(orderItem);
    }

    @Transactional
    public OrderItemResponse deleteByOrderItemId(Long id) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order item not found with id: " + id
                ));

        Product product = orderItem.getProduct();
        Order order = orderItem.getOrder();

        product.setStockQuantity(product.getStockQuantity() + orderItem.getQuantity());
        order.setTotalAmount(order.getTotalAmount().subtract(orderItem.getSubtotal()));

        productRepository.save(product);
        orderRepository.save(order);
        orderItemRepository.delete(orderItem);

        return OrderItemMapper.toOrderItemResponse(orderItem);
    }
}
